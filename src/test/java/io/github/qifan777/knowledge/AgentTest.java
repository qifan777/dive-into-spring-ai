package io.github.qifan777.knowledge;

import io.qifan.ai.dashscope.DashScopeAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AgentTest {
    @Autowired
    DashScopeAiChatModel chatModel;

    @Test
    public void agentTest() {
        ChatClient.create(chatModel)
            .prompt()
            .user("请问当前的日期是多少？")
            .functions("chronologist", "computerAssistant")
            .stream()
            .content()
            .log()
            .blockLast();
    }

    @Test
    public void dateFunctionTest() {
        ChatClient.create(chatModel)
            .prompt()
            .user("请问当前的日期是多少？")
            .functions("currentDate", "currentTime")
            .stream()
            .content()
            .log()
            .blockLast();
    }

}