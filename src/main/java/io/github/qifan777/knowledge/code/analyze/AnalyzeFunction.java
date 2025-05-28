package io.github.qifan777.knowledge.code.analyze;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.github.qifan777.knowledge.code.graph.entity.MethodNode;
import io.github.qifan777.knowledge.code.graph.service.CodeGraphService;
import io.github.qifan777.knowledge.infrastructure.code.CodeAssistantProperties;
import io.github.qifan777.knowledge.infrastructure.code.JavaParserUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Description("评审分析给定的java文件")
@Service
@AllArgsConstructor
@Slf4j
public class AnalyzeFunction implements Function<AnalyzeFunction.Request, String> {
    private final CodeAssistantProperties properties;
    private final CodeGraphService codeGraphService;
    // 防止页面多次调用开启多个线程token消耗过多，demo使用单线程
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final ChatModel chatModel;

    public record Request(@JsonProperty(required = true)
                          @JsonPropertyDescription("java文件路径如：src/main/java/xxx.java") String path) {
    }

    /**
     * 重定向到下面的网页进行展示分析结果，由于一个类里面可能有很多个方法，分析的单位是按照方法来分析，因此单独做一个页面来展示分析结果。
     *
     * @param request the function argument
     * @return 重定向链接
     */
    @Override
    public String apply(Request request) {
        return "请在下面的网页链接查看评审结果：http://localhost:5177/#/analyze?path=" + request.path;
    }

    /**
     * 创建一个结果分析流，另起一个线程开启解析java文件，获取主类中的所有方法，然后分析方法调用得到分析结果，推流到前端。
     *
     * @param filePath java文件路径 如：io.qifan.github777.UserController.java
     * @return Flux<AnalyzeResult> 结果分析流
     */
    @SneakyThrows
    public Flux<AnalyzeResult> analyze(String filePath) {
        log.info("正在评审文件：{}", filePath);
        return Flux.create(fluxSink -> executor.execute(() -> {
            JavaParserUtils.parse(Path.of(properties.getProject().getProjectPath(), "src", "main", "java", filePath))
                    .getResult()
                    .map(compilationUnit -> compilationUnit.findAll(ClassOrInterfaceDeclaration.class))
                    // 只分析主类, 可能java文件中一个类都没有，因此返回的是一个Optional。如果不使用FlatMap，会直接返回一个Optional<Optional<ClassOrInterfaceDeclaration>>，
                    // 因此需要使用flatMap，是的返回结果变成Optional<ClassOrInterfaceDeclaration>
                    .flatMap(classOrInterfaceDeclarations -> classOrInterfaceDeclarations.stream().findFirst())
                    // 由于类可能是匿名内部类，这边过滤一下。下面可以直接使用get()。当然实际上这个地方的主类类名肯定是存在的，但是为了写法严谨我还是判断了一下
                    .filter(classOrInterfaceDeclaration -> classOrInterfaceDeclaration.getFullyQualifiedName().isPresent())
                    // 获取到类名，并遍历所有方法
                    .map(classOrInterfaceDeclaration -> {
                        String fullyQualifiedName = classOrInterfaceDeclaration
                                .getFullyQualifiedName().get();
                        return classOrInterfaceDeclaration
                                .getMethods()
                                .stream()
                                .map(methodDeclaration -> fullyQualifiedName + "#" + methodDeclaration.getNameAsString())
                                .toList();
                    })
                    .ifPresentOrElse(methodIds -> {
                        methodIds.forEach(methodId -> {
                            analyzeMethod(methodId).doOnNext(fluxSink::next).blockLast();
                        });
                    }, () -> {
                        // 如果没有主类（Class Or Interface），则直接分析整个文件，或者不是Java文件（Mapper.xml）
                        fluxSink.next(analyzeFile(filePath));
                    });
            // 完成
            fluxSink.complete();
        }));
    }

    /**
     * 分析单个方法调用
     *
     * @param methodId 方法ID
     * @return 分析结果流
     */
    public Flux<AnalyzeResult> analyzeMethod(String methodId) {
        List<MethodNode> childMethods = codeGraphService.findChildMethods(methodId);
        String prompt = new PromptTemplate("""
                请你根据{methodId}的调用链评审代码，并给出你的改进建议，并且附带修改后的代码片段，用中文回答。
                {methodChains}
                """)
                .createMessage(Map.of("methodChains", childMethods
                                .stream()
                                .map(MethodNode::getContent)
                                .distinct()
                                .collect(Collectors.joining("\n")),
                        "methodId", methodId))
                .getText();
        log.info("评审方法: {}", prompt);
        String content = childMethods.stream().filter(m -> m.getId().equals(methodId)).findFirst().orElseThrow().getContent();
        return chatModel.stream(prompt).map(response -> new AnalyzeResult(methodId, response, methodId.split("#")[0], content));
    }

    public AnalyzeResult analyzeFile(String filePath) {
        String fileContent = FileUtil.readString(Path.of(properties.getProject().getProjectPath(), filePath).toFile(), StandardCharsets.UTF_8);
        String prompt = new PromptTemplate("""
                请你评审一下该提交文件是否有可以改进的地方，并且附带修改后的代码片段，没有请回答无，用中文回答。
                {content}
                """)
                .createMessage(Map.of("content", fileContent)).getText();
        log.info("评审文件提示词: {}", prompt);
        String result = chatModel.call(prompt);
        log.info("文件分析结果: {}", result);
        return new AnalyzeResult(filePath, result, filePath, fileContent);
    }

    @Data
    @AllArgsConstructor
    public static class AnalyzeResult {
        private String id;
        private String content;
        private String fileName;
        private String fileContent;
    }


}