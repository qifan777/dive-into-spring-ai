package io.github.qifan777.knowledge.code.analyze;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("analyze")
@AllArgsConstructor
public class AnalyzeController {
    private final AnalyzeFunction analyzeFunction;
    private final ObjectMapper objectMapper;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> analyzeTask(@RequestParam String path) {
        return analyzeFunction.analyze(path)
                .map(content -> ServerSentEvent.builder(toJson(content))
                        .event("message")
                        .build());
    }

    @SneakyThrows
    public String toJson(AnalyzeFunction.AnalyzeResult result) {
        return objectMapper.writeValueAsString(result);
    }
}
