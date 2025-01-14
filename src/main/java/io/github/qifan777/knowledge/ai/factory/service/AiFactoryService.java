package io.github.qifan777.knowledge.ai.factory.service;

import io.github.qifan777.knowledge.ai.factory.repository.AiFactoryRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class AiFactoryService {
    private final AiFactoryRepository aiFactoryRepository;

}