package io.github.qifan777.knowledge.code;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.qifan777.knowledge.ai.agent.AbstractAgent;
import io.github.qifan777.knowledge.ai.agent.Agent;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Description;

@Description("提供有关于Java代码的评审分析，在线诊断异常相关的回答")
@Agent
public class CodeAssistantAgent extends AbstractAgent<CodeAssistantAgent.Request, String> {
    /**
     * 构建ChatClient方便子类使用
     *
     * @param chatModel 聊天模型
     */
    public CodeAssistantAgent(ChatModel chatModel) {
        super(chatModel);
    }

    @Override
    public String apply(Request request) {
        return getChatClient()
                .prompt()
                .user(request.query())
                .functions("analyzeFunction", "arthasFunction")
                .call()
                .content();
    }

    public record Request(
            @JsonProperty(required = true) @JsonPropertyDescription(value = "用户原始的提问") String query) {
    }
}
