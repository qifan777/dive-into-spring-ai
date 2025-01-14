package io.github.qifan777.knowledge.ai.model.entity;

import io.github.qifan777.knowledge.ai.factory.entity.AiFactory;
import io.github.qifan777.knowledge.infrastructure.jimmer.BaseEntity;
import io.qifan.infrastructure.generator.core.GenEntity;
import io.qifan.infrastructure.generator.core.GenTextField;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.ManyToOne;

/**
 * 模型名称
 */
@GenEntity
@Entity
public interface AiModel extends BaseEntity {
    /**
     * 模型名称
     */
    @GenTextField(label = "模型名称")
    String name();

    /**
     * 英文名称
     */
    @GenTextField(label = "模型英文名称")
    String value();

    /**
     * 厂家
     */
    @ManyToOne
    AiFactory factory();

    @IdView
    String factoryId();
}
