package io.github.qifan777.knowledge.ai.model.controller;

import io.github.qifan777.knowledge.ai.model.entity.AiModel;
import io.github.qifan777.knowledge.ai.model.entity.dto.AiModelInput;
import io.github.qifan777.knowledge.ai.model.entity.dto.AiModelSpec;
import io.github.qifan777.knowledge.ai.model.repository.AiModelRepository;
import io.github.qifan777.knowledge.infrastructure.model.QueryRequest;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.FetchBy;
import org.babyfish.jimmer.client.meta.DefaultFetcherOwner;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ai-model")
@AllArgsConstructor
@DefaultFetcherOwner(AiModelRepository.class)
@Transactional
public class AiModelController {
    private final AiModelRepository aiModelRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "FETCHER") AiModel findById(@PathVariable String id) {
        return aiModelRepository.findById(id, AiModelRepository.FETCHER).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "FETCHER") AiModel> query(@RequestBody QueryRequest<AiModelSpec> queryRequest) {
        return aiModelRepository.findPage(queryRequest, AiModelRepository.FETCHER);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated AiModelInput aiModelInput) {
        return aiModelRepository.save(aiModelInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        aiModelRepository.deleteAllById(ids);
        return true;
    }
}