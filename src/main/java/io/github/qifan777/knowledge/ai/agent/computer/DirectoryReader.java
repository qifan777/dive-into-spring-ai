package io.github.qifan777.knowledge.ai.agent.computer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

@Component
@Description("读取用户给定的文件夹，列出文件夹下的所有文件")
public class DirectoryReader implements Function<DirectoryReader.Request, String> {
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
