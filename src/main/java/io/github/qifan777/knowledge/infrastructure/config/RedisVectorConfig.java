package io.github.qifan777.knowledge.infrastructure.config;

import io.qifan.ai.dashscope.DashScopeAiEmbeddingModel;
import lombok.AllArgsConstructor;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreAutoConfiguration;
import org.springframework.ai.autoconfigure.vectorstore.redis.RedisVectorStoreProperties;
import org.springframework.ai.vectorstore.RedisVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// 禁用SpringAI提供的RedisStack向量数据库的自动配置，会和Redis的配置冲突。
@EnableAutoConfiguration(exclude = {RedisVectorStoreAutoConfiguration.class})
// 读取RedisStack的配置信息
@EnableConfigurationProperties({RedisVectorStoreProperties.class})
@AllArgsConstructor
public class RedisVectorConfig {

    /**
     * 创建RedisStack向量数据库
     *
     * @param embeddingModel 嵌入模型
     * @param properties     redis-stack的配置信息
     * @return vectorStore 向量数据库
     */
    @Bean
    public VectorStore vectorStore(DashScopeAiEmbeddingModel embeddingModel,
                                   RedisVectorStoreProperties properties) {
        var config = RedisVectorStore.RedisVectorStoreConfig
            .builder()
            .withURI(properties.getUri())
            .withIndexName(properties.getIndex())
            .withPrefix(properties.getPrefix()).build();
        return new RedisVectorStore(config, embeddingModel, properties.isInitializeSchema());
    }
}
