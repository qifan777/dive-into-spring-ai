package io.github.qifan777.knowledge.graph.manager;

import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ManagerRepository extends Neo4jRepository<Manager, String> {
}
