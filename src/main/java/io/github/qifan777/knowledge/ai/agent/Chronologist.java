package io.github.qifan777.knowledge.ai.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import io.qifan.ai.dashscope.DashScopeAiChatModel;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Function;

@Component
@Description(value = "回答用户有关于时间的提问")
@Slf4j
public class Chronologist extends AbstractAgent<Chronologist.Request, String> {
    private final String SYSTEM = """
            你是一个专业的编年史学家，可以回答有关时间的问题。
            您还可以执行各种与时间相关的任务，如转换和格式化。
            """;

    public Chronologist(DashScopeAiChatModel chatModel) {
        super(chatModel);
    }

    @Override
    public String apply(Request request) {
        log.info("使用日期Agent");
        return getChatClient()
                .prompt()
                .system(SYSTEM)
                .user(request.query)
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
            String formatted = currentDate.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            log.info("获取当前的时间:{}", formatted);
            return formatted;

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
            val formatted = currentDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            log.info("获取当前日期：{}", formatted);
            return formatted;
        }

        public record Request() {
        }
    }
}