package io.github.qifan777.knowledge.ai.session;

import io.github.qifan777.knowledge.ai.message.AiMessage;
import io.github.qifan777.knowledge.ai.message.dto.AiMessageParams;
import io.github.qifan777.knowledge.infrastructure.jimmer.BaseEntity;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.OneToMany;
import org.babyfish.jimmer.sql.OrderedProp;
import org.babyfish.jimmer.sql.Serialized;

import java.util.List;

/**
 * 会话
 */
@Entity
public interface AiSession extends BaseEntity {

    /**
     * 会话名称
     */
    String name();

    @Serialized
    AiMessageParams params();

    /**
     * 一对多关联消息，按创建时间升序
     */

    @OneToMany(mappedBy = "session", orderedProps = @OrderedProp(value = "createdTime"))
    List<AiMessage> messages();
}

