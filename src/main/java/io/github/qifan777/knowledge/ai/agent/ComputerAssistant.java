package io.github.qifan777.knowledge.ai.agent;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Agent
@Description("提供关于当前主机的cpu，文件，文件夹相关问题的有用回答")
public class ComputerAssistant extends AbstractAgent<ComputerAssistant.Request, String> {


    protected ComputerAssistant(ChatModel chatModel) {
        super(chatModel);
    }

    @Override
    public String apply(Request request) {
        return getChatClient()
                .prompt()
                .user(request.query())
                .call()
                .content();
    }

    public record Request(
            @JsonProperty(required = true) @JsonPropertyDescription(value = "用户原始的提问") String query) {
    }

    @Component
    @Description("读取用户给定的文件夹，列出文件夹下的所有文件")
    public static class DirectoryReader implements Function<DirectoryReader.Request, String> {
        @Override
        public String apply(Request request) {
            File f = new File(request.path);
            List<String> out = new ArrayList<>();
            if (f.exists()) {
                String[] list = f.list();
                if (list != null) {
                    out = Arrays.asList(list);
                }
            }
            return String.join(",", out);
        }

        public record Request(
                @JsonProperty(required = true) @JsonPropertyDescription("本机文件夹的绝对路径") String path
        ) {
        }
    }

    @Component
    @Description("读取CPU的数量")
    public static class CpuAnalyzer implements Function<CpuAnalyzer.Request, Integer> {
        @Override
        public Integer apply(Request request) {
            return Runtime.getRuntime().availableProcessors();
        }

        public record Request() {
        }
    }
}