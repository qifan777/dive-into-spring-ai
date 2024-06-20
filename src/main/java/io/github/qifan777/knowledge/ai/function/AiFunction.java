package io.github.qifan777.knowledge.ai.function;

import io.github.qifan777.knowledge.infrastructure.jimmer.BaseDateTime;
import io.github.qifan777.knowledge.infrastructure.jimmer.UUIDIdGenerator;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;

/**
 * AI函数
 */
@Entity
public interface AiFunction extends BaseDateTime {
    @Id
    @GeneratedValue(generatorType = UUIDIdGenerator.class)
    String id();


    /**
     * 函数名称
     */
    String name();

    /**
     * 函数描述
     */
    String description();
}

