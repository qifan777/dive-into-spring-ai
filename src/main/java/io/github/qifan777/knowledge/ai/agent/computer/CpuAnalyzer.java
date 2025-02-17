package io.github.qifan777.knowledge.ai.agent.computer;

import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@Description("读取CPU的数量")
public class CpuAnalyzer implements Function<CpuAnalyzer.Request, Integer> {
    @Override
    public Integer apply(Request request) {
        return Runtime.getRuntime().availableProcessors();
    }

    public record Request() {
    }
}