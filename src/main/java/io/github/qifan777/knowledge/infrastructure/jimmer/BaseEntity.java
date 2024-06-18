package io.github.qifan777.knowledge.infrastructure.jimmer;


import io.github.qifan777.knowledge.user.User;
import org.babyfish.jimmer.sql.*;

@MappedSuperclass
public interface BaseEntity extends BaseDateTime {

    @Id
    @GeneratedValue(generatorType = UUIDIdGenerator.class)
    String id();

    @ManyToOne
    @OnDissociate(DissociateAction.DELETE)
    User editor();

    @ManyToOne
    @OnDissociate(DissociateAction.DELETE)
    User creator();
}