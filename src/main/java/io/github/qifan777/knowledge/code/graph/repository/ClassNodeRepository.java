package io.github.qifan777.knowledge.code.graph.repository;

import io.github.qifan777.knowledge.code.graph.entity.ClassNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ClassNodeRepository extends Neo4jRepository<ClassNode,String> {
}
