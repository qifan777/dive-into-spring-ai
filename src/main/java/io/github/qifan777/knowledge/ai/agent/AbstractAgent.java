package io.github.qifan777.knowledge.ai.agent;

import io.qifan.ai.dashscope.DashScopeAiChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Description;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Slf4j
public abstract class AbstractAgent<Req, Resp> implements Function<Req, Resp> {

    private final ChatClient client;

    public AbstractAgent(DashScopeAiChatModel chatModel) {
        this.client = ChatClient
                .builder(chatModel)
                .defaultFunctions(getFunctions())
                .build();

    }

    public ChatClient getChatClient() {
        return client;
    }

    public String[] getFunctions() {
        List<Class<?>> classList = Arrays.stream(this.getClass().getClasses()).filter(aClass -> aClass.isAnnotationPresent(Description.class)).toList();
        String[] names = new String[classList.size()];
        classList.stream().map(aClass -> StringUtils.uncapitalize(this.getClass().getSimpleName()) + "." + aClass.getSimpleName()).toList().toArray(names);
        return names;
    }
}