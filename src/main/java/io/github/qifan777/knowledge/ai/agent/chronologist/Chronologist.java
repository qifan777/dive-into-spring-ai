package io.github.qifan777.knowledge.ai.agent.chronologist;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.github.qifan777.knowledge.ai.agent.AbstractAgent;
import io.github.qifan777.knowledge.ai.agent.Agent;
import lombok.AllArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Agent
@Description(value = "回答用户有关于日期、时间的提问")
@AllArgsConstructor
public class Chronologist extends AbstractAgent implements Function<Chronologist.Request, String> {
    private final String SYSTEM = """
            你是一个专业的编年史学家，可以回答有关时间的问题。
            您还可以执行各种与时间相关的任务，如转换和格式化。
            """;
    private final ChatModel chatModel;


    @Override
    public String apply(Request request) {
        return ChatClient.create(chatModel)
                .prompt()
                .system(SYSTEM)
                .user(request.query)
                .functions(getAgentFunctions(this.getClass()))
                .call()
                .content();
    }

    public record Request(
            @JsonProperty(required = true) @JsonPropertyDescription(value = "用户原始的提问") String query) {
    }

    @Component
    @Description("获取当前的时间，格式是 HH:mm:ss")
    public static class CurrentTime implements Function<CurrentTime.Request, String> {
        @Override
        public String apply(Request request) {
            LocalDateTime currentDate = LocalDateTime.now();
            return currentDate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        }

        public record Request() {
        }
    }

    @Component
    @Description("获取当前的日期，格式是 yyyy-MM-dd")
    public static class CurrentDate implements Function<CurrentDate.Request, String> {
        @Override
        public String apply(Request request) {
            LocalDate currentDate = LocalDate.now();
            return currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }

        public record Request() {
        }
    }
}