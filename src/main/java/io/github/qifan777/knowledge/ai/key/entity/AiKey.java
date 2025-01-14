package io.github.qifan777.knowledge.ai.key.entity;

import io.github.qifan777.knowledge.ai.factory.entity.AiFactory;
import io.github.qifan777.knowledge.infrastructure.jimmer.BaseEntity;
import io.qifan.infrastructure.generator.core.GenAssociationField;
import io.qifan.infrastructure.generator.core.GenEntity;
import io.qifan.infrastructure.generator.core.GenNumberField;
import io.qifan.infrastructure.generator.core.GenTextField;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.IdView;
import org.babyfish.jimmer.sql.ManyToOne;

import java.math.BigDecimal;

/**
 * AI Api kEY
 */
@GenEntity
@Entity
public interface AiKey extends BaseEntity {
    /**
     * 名称
     */
    @GenTextField(label = "名称", order = 1)
    String name();

    /**
     * api key
     */
    @GenTextField(label = "api key", order = 2)
    String value();

    /**
     * 厂家
     */
    @GenAssociationField(label = "AI厂家", order = 3, prop = "factoryId")
    @ManyToOne
    AiFactory factory();

    @IdView
    String factoryId();

    /**
     * 已使用token
     */
    @GenNumberField(label = "已使用token", order = 4)
    @Null
    BigDecimal usageToken();

    /**
     * 总token
     */
    @GenNumberField(label = "总token", order = 5)
    @Null
    BigDecimal totalToken();
}
