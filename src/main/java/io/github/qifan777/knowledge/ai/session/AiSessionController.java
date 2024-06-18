package io.github.qifan777.knowledge.ai.session;

import io.github.qifan777.knowledge.ai.session.dto.AiSessionInput;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import org.babyfish.jimmer.client.FetchBy;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("session")
@RestController
@AllArgsConstructor
public class AiSessionController {
    private final AiSessionRepository sessionRepository;

    @GetMapping("{id}")
    public @FetchBy(value = "FETCHER", ownerType = AiSessionRepository.class) AiSession findById(@PathVariable String id) {
        return sessionRepository.findById(id).orElseThrow(() -> new BusinessException("会话不存在"));
    }

    @PostMapping("save")
    public String save(@RequestBody AiSessionInput input) {
        return sessionRepository.save(input.toEntity()).id();
    }

    @GetMapping("user")
    public List<@FetchBy(value = "FETCHER", ownerType = AiSessionRepository.class) AiSession> findByUser() {
        return sessionRepository.findByUser();
    }

    @DeleteMapping
    public void delete(@RequestBody List<String> ids) {
        sessionRepository.deleteByIds(ids);
    }
}
