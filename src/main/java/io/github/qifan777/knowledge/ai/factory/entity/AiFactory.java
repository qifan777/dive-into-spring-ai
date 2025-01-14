package io.github.qifan777.knowledge.ai.factory.entity;

import io.github.qifan777.knowledge.infrastructure.jimmer.BaseEntity;
import io.qifan.infrastructure.generator.core.GenEntity;
import io.qifan.infrastructure.generator.core.GenTextField;
import org.babyfish.jimmer.sql.Entity;

/**
 * AI厂家
 */
@GenEntity
@Entity
public interface AiFactory extends BaseEntity {
    /**
     * 名称
     */
    @GenTextField(label = "名称", order = 1)
    String name();

    /**
     * 英文名称
     */
    @GenTextField(label = "英文名称", order = 2)
    String value();
}
