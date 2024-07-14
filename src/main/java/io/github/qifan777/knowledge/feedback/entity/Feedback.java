package io.github.qifan777.knowledge.feedback.entity;

import io.github.qifan777.knowledge.infrastructure.jimmer.BaseEntity;
import io.qifan.infrastructure.generator.core.GenEntity;
import io.qifan.infrastructure.generator.core.GenField;
import io.qifan.infrastructure.generator.core.ItemType;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.Serialized;

import java.util.List;

/**
 * Entity for table "feedback"
 */
@GenEntity
@Entity
public interface Feedback extends BaseEntity {
    @GenField(value = "反馈内容", order = 0)
    String content();

    @GenField(value = "反馈图片", type = ItemType.PICTURE)
    @Serialized
    List<String> pictures();
}

