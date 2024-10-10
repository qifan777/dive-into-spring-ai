package io.github.qifan777.knowledge.code.graph.repository;

import io.github.qifan777.knowledge.code.graph.entity.MethodNode;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MethodNodeRepository extends Neo4jRepository<MethodNode, String> {

}
