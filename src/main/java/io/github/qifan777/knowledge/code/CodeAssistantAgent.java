package io.github.qifan777.knowledge.code;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.qifan777.knowledge.ai.agent.AbstractAgent;
import io.github.qifan777.knowledge.ai.agent.Agent;
import io.github.qifan777.knowledge.code.analyze.AnalyzeFunction;
import io.github.qifan777.knowledge.code.arthas.ArthasFunction;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

@Description("提供有关于Java代码的评审分析，在线诊断异常相关的回答")
@Agent
@AllArgsConstructor
public class CodeAssistantAgent extends AbstractAgent implements Function<CodeAssistantAgent.Request, String> {
    private final ChatModel chatModel;

    @Override
    public String apply(Request request) {
        return ChatClient.create(chatModel)
                .prompt()
                .user(request.query())
                .toolNames(getFunctions(AnalyzeFunction.class, ArthasFunction.class))
                .call()
                .content();
    }

    public record Request(
            @JsonProperty(required = true) @JsonPropertyDescription(value = "用户原始的提问") String query) {
    }
}
