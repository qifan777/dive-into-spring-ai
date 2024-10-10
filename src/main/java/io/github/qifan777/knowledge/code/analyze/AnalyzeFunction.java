package io.github.qifan777.knowledge.code.analyze;

import cn.hutool.core.io.FileUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import io.github.qifan777.knowledge.code.graph.entity.MethodNode;
import io.github.qifan777.knowledge.code.graph.service.GraphService;
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
import java.util.Objects;
import java.util.Optional;
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
    private final GraphService graphService;
    // 防止页面多次调用开启多个线程token消耗过多，demo使用单线程
    private final Executor executor = Executors.newSingleThreadExecutor();
    private final ChatModel chatModel;

    @SneakyThrows
    @Override
    public String apply(Request request) {
        return "清在下面的网页链接查看评审结果：http://localhost:5177/#/analyze?path=" + request.path;
    }

    public record Request(@JsonProperty(required = true)
                          @JsonPropertyDescription("java文件路径如：src/main/java/xxx.java") String path) {
    }


    @SneakyThrows
    public Flux<AnalyzeResult> analyze(String filePath) {
        log.info("正在评审文件：{}", filePath);
        Optional<CompilationUnit> result = JavaParserUtils.getJavaParser().parse(Path.of(properties.getProject().getProjectPath(), filePath))
                .getResult();
        return Flux.create(fluxSink -> executor.execute(() -> {
            result.map(compilationUnit -> compilationUnit.findAll(ClassOrInterfaceDeclaration.class))
                    .filter(list -> !list.isEmpty())
                    .map(classOrInterfaceDeclarations -> classOrInterfaceDeclarations.get(0))
                    .flatMap(classOrInterfaceDeclaration -> classOrInterfaceDeclaration
                            .getFullyQualifiedName()
                            .map(className -> classOrInterfaceDeclaration
                                    .getMethods()
                                    .stream()
                                    .map(methodDeclaration -> classOrInterfaceDeclaration
                                                                      .getFullyQualifiedName()
                                                                      .orElseThrow()
                                                              + "#" + methodDeclaration.getNameAsString())
                                    .toList()))
                    .ifPresentOrElse(methodIds -> {
                        methodIds.forEach(methodId -> {
                            analyzeMethod(methodId).doOnEach(analyzeResultSignal -> {
                                if (analyzeResultSignal.hasValue()) {
                                    fluxSink.next(Objects.requireNonNull(analyzeResultSignal.get()));
                                }
                            }).blockLast();
                        });
                    }, () -> {
                        fluxSink.next(analyzeFile(filePath));
                    });
            fluxSink.complete();
        }));
    }

    public Flux<AnalyzeResult> analyzeMethod(String methodId) {
        List<MethodNode> childMethods = graphService.findChildMethods(methodId);
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
                .getContent();
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
                .createMessage(Map.of("content", fileContent)).getContent();
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