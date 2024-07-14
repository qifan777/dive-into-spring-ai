package io.github.qifan777.knowledge.feedback.service;

import io.github.qifan777.knowledge.feedback.repository.FeedbackRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

}