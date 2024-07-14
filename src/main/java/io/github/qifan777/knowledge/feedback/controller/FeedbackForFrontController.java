package io.github.qifan777.knowledge.feedback.controller;

import cn.dev33.satoken.stp.StpUtil;
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
@RequestMapping("front/feedback")
@AllArgsConstructor
@DefaultFetcherOwner(FeedbackRepository.class)
@Transactional
public class FeedbackForFrontController {
    private final FeedbackRepository feedbackRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") Feedback findById(@PathVariable String id) {
        return feedbackRepository.findById(id, FeedbackRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
    }

    @PostMapping("query")
    public Page<@FetchBy(value = "COMPLEX_FETCHER_FOR_FRONT") Feedback> query(@RequestBody QueryRequest<FeedbackSpec> queryRequest) {
        queryRequest.getQuery().setCreatorId(StpUtil.getLoginIdAsString());
        return feedbackRepository.findPage(queryRequest, FeedbackRepository.COMPLEX_FETCHER_FOR_FRONT);
    }

    @PostMapping
    public String create(@RequestBody @Validated FeedbackCreateInput feedbackCreateInput) {
        return feedbackRepository.save(feedbackCreateInput).id();
    }

    @PutMapping
    public String update(@RequestBody @Validated FeedbackUpdateInput feedbackUpdateInput) {
        Feedback feedback = feedbackRepository.findById(feedbackUpdateInput.getId(), FeedbackRepository.COMPLEX_FETCHER_FOR_FRONT).orElseThrow(() -> new BusinessException("数据不存在"));
        if (!feedback.creator().id().equals(StpUtil.getLoginIdAsString())) {
            throw new BusinessException("只能修改自己的数据");
        }
        return feedbackRepository.save(feedbackUpdateInput).id();
    }

    @DeleteMapping
    public Boolean delete(@RequestBody List<String> ids) {
        feedbackRepository.findByIds(ids, FeedbackRepository.COMPLEX_FETCHER_FOR_FRONT).forEach(feedback -> {
            if (!feedback.creator().id().equals(StpUtil.getLoginIdAsString())) {
                throw new BusinessException("只能删除自己的数据");
            }
        });
        feedbackRepository.deleteAllById(ids);
        return true;
    }
}
