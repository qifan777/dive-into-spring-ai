package io.github.qifan777.knowledge.graph;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface FormRepository extends Neo4jRepository<Form, String> {
}
