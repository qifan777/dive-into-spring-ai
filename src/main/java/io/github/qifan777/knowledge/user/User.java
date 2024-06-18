package io.github.qifan777.knowledge.user;

import io.github.qifan777.knowledge.infrastructure.jimmer.BaseDateTime;
import io.github.qifan777.knowledge.infrastructure.jimmer.UUIDIdGenerator;
import jakarta.validation.constraints.Null;
import org.babyfish.jimmer.sql.Entity;
import org.babyfish.jimmer.sql.GeneratedValue;
import org.babyfish.jimmer.sql.Id;
import org.babyfish.jimmer.sql.Key;


@Entity
public interface User extends BaseDateTime {
    @Id
    @GeneratedValue(generatorType = UUIDIdGenerator.class)
    String id();

    /**
     * 手机号
     */
    @Key
    String phone();

    /**
     * 密码
     */
    String password();

    /**
     * 头像
     */
    @Null
    String avatar();

    /**
     * 昵称
     */
    @Null
    String nickname();

    /**
     * 性别
     */
    @Null
    String gender();
}

