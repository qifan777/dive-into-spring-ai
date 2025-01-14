package io.github.qifan777.knowledge.ai.key.service;

import io.github.qifan777.knowledge.ai.key.repository.AiKeyRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
@Transactional
public class AiKeyService {
    private final AiKeyRepository aiKeyRepository;

}