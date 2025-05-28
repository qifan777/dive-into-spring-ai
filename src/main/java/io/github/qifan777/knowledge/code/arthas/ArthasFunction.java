package io.github.qifan777.knowledge.code.arthas;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.databind.JsonNode;
import io.github.qifan777.knowledge.code.graph.entity.MethodNode;
import io.github.qifan777.knowledge.code.graph.service.CodeGraphService;
import io.github.qifan777.knowledge.infrastructure.code.CodeAssistantProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.context.annotation.Description;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
@Description("诊断方法出现异常的原因")
public class ArthasFunction implements Function<ArthasFunction.Request, String> {
    private final CodeGraphService codeGraphService;
    private final CodeAssistantProperties properties;
    private final ChatModel chatModel;

    public record Request(@JsonProperty(required = true)
                          @JsonPropertyDescription("类名") String className,
                          @JsonProperty(required = true)
                          @JsonPropertyDescription("类名") String methodName) {
    }

    @Override
    public String apply(Request request) {
        String methodId = request.className + "#" + request.methodName;
        log.info("监听目标：{}", methodId);
        JobResult jobResult = startWatch(request.className, request.methodName);
        if (jobResult == null) {
            return "无异常信息";
        }
        String analyzeResult = jobResult
                .getBody()
                .getResults()
                .stream()
                .filter(r -> r.getType().equals("tt"))
                .findFirst()
                .map(result -> {
                    String methods = codeGraphService.findChildMethods(methodId).stream().map(MethodNode::getContent)
                            .distinct()
                            .collect(Collectors.joining("\n"));
                    TimeFragment timeFragment = result.getTimeFragmentList().get(0);
                    String content = new PromptTemplate("""
                            根据下面提供的内容分析异常原因，回答结果用中文
                            方法名称：{methodName}
                            方法调用链: {methods}
                            方法参数：{params}
                            异常信息：{exp}
                            """)
                            .createMessage(Map.of("methodName", timeFragment.getMethodName(),
                                    "methods", methods,
                                    "params", timeFragment.getParams().stream().map(param -> param.getObject() == null ? "" : param.getObject().toString()).collect(Collectors.joining("\n")),
                                    "exp", timeFragment.getThrowExp()))
                            .getText();
                    log.info("代码诊断prompt: {}", content);
                    return chatModel.call(content);
                })
                .orElse("无异常信息");
        log.info("诊断结果: {}", analyzeResult);
        return analyzeResult;
    }


    public JobResult startWatch(String className, String method) {
        CodeAssistantProperties.ArthasProperties arthasProperties = properties.getArthas();
        RestTemplate restTemplate = new RestTemplate();
        String encode = Base64.getEncoder().encodeToString((arthasProperties.getUsername() + ":" + arthasProperties.getPassword()).getBytes(StandardCharsets.UTF_8));
        ArthasRequest arthasRequest = new ArthasRequest().setAction("exec").setCommand("tt -t " + className + " " + method + " -n 1");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Basic " + encode);
        HttpEntity<ArthasRequest> requestHttpEntity = new HttpEntity<>(arthasRequest, httpHeaders);
        return restTemplate.exchange(arthasProperties.getUrl(), HttpMethod.POST, requestHttpEntity, JobResult.class).getBody();

    }

    @Accessors(chain = true)
    @Data
    public static class ArthasRequest {
        private String action;
        private String command;
    }

    @Data
    public static class JobResult {
        @JsonProperty("body")
        private Body body;
        private String message;
        private String sessionId;
        private String state;
    }

    @Data
    public static class Body {
        @JsonProperty("command")
        private String command;
        private int jobId;
        private String jobStatus;
        @JsonProperty("results")
        private List<Result> results;
        @JsonProperty("timeExpired")
        private boolean timeExpired;
        private int timeout;
    }

    @Data
    public static class Result {
        private Effect effect;
        private int jobId;
        private boolean success;
        private String type;
        @JsonProperty("timeFragmentList")
        private List<TimeFragment> timeFragmentList;
        private int statusCode;
        private boolean first;
    }

    @Data
    public static class Effect {
        private int classCount;
        private int cost;
        private int listenerId;
        private int methodCount;
    }

    @Data
    public static class TimeFragment {
        private String className;
        private double cost;
        private int index;
        private String methodName;
        private String object;
        private List<Param> params;
        @JsonProperty("return")
        private boolean isReturn;
        private String returnObj;
        @JsonProperty("throw")
        private boolean isThrow;
        @JsonProperty("throwExp")
        private String throwExp;
        @JsonProperty("timestamp")
        private String timestamp;
    }

    @Data
    public static class Param {
        private int expand;
        private JsonNode object;
    }

}
