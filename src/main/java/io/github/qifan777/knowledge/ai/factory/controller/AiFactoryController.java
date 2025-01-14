package io.github.qifan777.knowledge.ai.factory.controller;

import io.github.qifan777.knowledge.ai.factory.entity.AiFactory;
import io.github.qifan777.knowledge.ai.factory.entity.dto.AiFactoryInput;
import io.github.qifan777.knowledge.ai.factory.entity.dto.AiFactorySpec;
import io.github.qifan777.knowledge.ai.factory.repository.AiFactoryRepository;
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
@RequestMapping("/ai-factory")
@AllArgsConstructor
@DefaultFetcherOwner(AiFactoryRepository.class)
@Transactional
public class AiFactoryController {
    private final AiFactoryRepository aiFactoryRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "FETCHER") AiFactory findById(@PathVariable String id) {
        return aiFactoryRepository.findById(id, AiFactoryRepository.FETCHER).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "FETCHER") AiFactory> query(@RequestBody QueryRequest<AiFactorySpec> queryRequest) {
        return aiFactoryRepository.findPage(queryRequest, AiFactoryRepository.FETCHER);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated AiFactoryInput aiFactoryInput) {
        return aiFactoryRepository.save(aiFactoryInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        aiFactoryRepository.deleteAllById(ids);
        return true;
    }
}