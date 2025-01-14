package io.github.qifan777.knowledge.ai.key.controller;

import io.github.qifan777.knowledge.ai.key.entity.AiKey;
import io.github.qifan777.knowledge.ai.key.entity.dto.AiKeyInput;
import io.github.qifan777.knowledge.ai.key.entity.dto.AiKeySpec;
import io.github.qifan777.knowledge.ai.key.repository.AiKeyRepository;
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
@RequestMapping("/ai-key")
@AllArgsConstructor
@DefaultFetcherOwner(AiKeyRepository.class)
@Transactional
public class AiKeyController {
    private final AiKeyRepository aiKeyRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "FETCHER") AiKey findById(@PathVariable String id) {
        return aiKeyRepository.findById(id, AiKeyRepository.FETCHER).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "FETCHER") AiKey> query(@RequestBody QueryRequest<AiKeySpec> queryRequest) {
        return aiKeyRepository.findPage(queryRequest, AiKeyRepository.FETCHER);
    }

    @PostMapping("save")
    public String save(@RequestBody @Validated AiKeyInput aiKeyInput) {
        return aiKeyRepository.save(aiKeyInput.toEntity()).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        aiKeyRepository.deleteAllById(ids);
        return true;
    }
}