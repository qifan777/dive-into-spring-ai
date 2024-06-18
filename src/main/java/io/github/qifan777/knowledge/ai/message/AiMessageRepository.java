package io.github.qifan777.knowledge.ai.message;

import org.babyfish.jimmer.spring.repository.JRepository;

import java.util.List;

public interface AiMessageRepository extends JRepository<AiMessage, String> {
    AiMessageTable t = AiMessageTable.$;

    default List<AiMessage> findBySessionId(String sessionId, int lastN) {
        return sql()
                .createQuery(t)
                .where(t.sessionId().eq(sessionId))
                .orderBy(t.createdTime().asc())
                .select(t)
                .limit(lastN)
                .execute();
    }

    default Integer deleteBySessionId(String sessionId) {
        return sql().createDelete(t)
                .where(t.sessionId().eq(sessionId))
                .execute();
    }
}
