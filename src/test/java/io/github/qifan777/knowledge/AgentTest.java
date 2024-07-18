package io.github.qifan777.knowledge;

import io.github.qifan777.knowledge.ai.agent.Chronologist;
import io.qifan.ai.dashscope.DashScopeAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.moonshot.MoonshotChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

@SpringBootTest
@Slf4j
public class AgentTest {
    @Autowired
    DashScopeAiChatModel chatModel;
    @Autowired
    Chronologist.CurrentDate currentDate;
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    MoonshotChatModel moonshotChatModel;

    @Test
    public void kimiTest() {
        log.info(moonshotChatModel.call("你好"));
    }

    @Test
    public void agentTest() {
        ChatClient.create(chatModel)
                .prompt()
                .user("C:\\Users\\Administrator\\Desktop\\资料，这个文件夹下有哪些文件？")
                .functions("chronologist", "computerAssistant")
                .stream()
                .content()
                .log()
                .blockLast();
    }

    @Test
    public void dateFunctionTest() {
        Chronologist chronologist = new Chronologist(chatModel);
        ChatClient.create(chatModel)
                .prompt()
                .user("请问当前的日期是多少？")
                .functions(chronologist.getFunctions())
                .stream()
                .content()
                .log()
                .blockLast();
    }

}