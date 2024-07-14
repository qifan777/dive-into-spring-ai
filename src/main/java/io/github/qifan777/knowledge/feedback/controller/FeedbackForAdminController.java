package io.github.qifan777.knowledge.feedback.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import io.github.qifan777.knowledge.feedback.entity.Feedback;
import io.github.qifan777.knowledge.feedback.entity.dto.FeedbackCreateInput;
import io.github.qifan777.knowledge.feedback.entity.dto.FeedbackSpec;
import io.github.qifan777.knowledge.feedback.entity.dto.FeedbackUpdateInput;
import io.github.qifan777.knowledge.feedback.repository.FeedbackRepository;
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
@RequestMapping("admin/feedback")
@AllArgsConstructor
@DefaultFetcherOwner(FeedbackRepository.class)
@Transactional
public class FeedbackForAdminController {
    private final FeedbackRepository feedbackRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") Feedback findById(@PathVariable String id) {
        return feedbackRepository.findById(id, FeedbackRepository.COMPLEX_FETCHER_FOR_ADMIN).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_ADMIN") Feedback> query(@RequestBody QueryRequest<FeedbackSpec> queryRequest) {
        return feedbackRepository.findPage(queryRequest, FeedbackRepository.COMPLEX_FETCHER_FOR_ADMIN);
    }

    @PostMapping
    public String create(@RequestBody @Validated FeedbackCreateInput feedbackInput) {
        return feedbackRepository.save(feedbackInput).id();
    }

    @PutMapping
    public String update(@RequestBody @Validated FeedbackUpdateInput feedbackInput) {
        return feedbackRepository.save(feedbackInput).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        feedbackRepository.deleteAllById(ids);
        return true;
    }
}