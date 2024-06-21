package io.github.qifan777.knowledge.ai.function;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.client.FetchBy;
import org.springframework.ai.model.function.FunctionCallback;
import org.springframework.ai.model.function.FunctionCallbackContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("function")
@AllArgsConstructor
@Slf4j
public class AiFunctionController {
    private final FunctionCallbackContext functionCallbackContext;
    private final AiFunctionRepository functionRepository;
    public static final AiFunctionFetcher FETCHER = AiFunctionFetcher.$.allScalarFields();
    public static final String[] FUNCTION_NAME_LIST = new String[]{
        "documentAnalyzerFunction"
    };

    /**
     * 每次启动自动注册
     */
    @PostConstruct
    public void register() {
        functionRepository.deleteAll();
        List<AiFunction> list = Arrays.stream(FUNCTION_NAME_LIST).map(name -> {
            FunctionCallback functionCallback = functionCallbackContext.getFunctionCallback(name, null);
            return AiFunctionDraft.$.produce(draft -> {
                draft.setName(functionCallback.getName())
                    .setDescription(functionCallback.getDescription());
            });
        }).toList();
        functionRepository.saveEntities(list);
    }

    @GetMapping("list")
    public List<@FetchBy(value = "FETCHER") AiFunction> list() {
        return functionRepository.findAll(FETCHER);
    }
}
