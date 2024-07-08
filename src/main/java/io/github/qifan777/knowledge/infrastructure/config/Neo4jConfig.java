package io.github.qifan777.knowledge.infrastructure.config;

import io.qifan.ai.dashscope.DashScopeAiEmbeddingModel;
import org.neo4j.driver.Driver;
import org.springframework.ai.vectorstore.Neo4jVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Neo4jConfig {
    @Bean
    public Neo4jVectorStore neo4jVectorStore(Driver driver, DashScopeAiEmbeddingModel embeddingModel) {
        return new Neo4jVectorStore(driver, embeddingModel, Neo4jVectorStore.Neo4jVectorStoreConfig.builder()
            .withDatabaseName("neo4j")
            .withEmbeddingProperty("textEmbedding")
            .withIndexName("form_10k_chunks")
            .withLabel("Chunk")
            .build(), false);
    }
}