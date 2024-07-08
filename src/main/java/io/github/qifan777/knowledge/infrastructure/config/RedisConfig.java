package io.github.qifan777.knowledge.infrastructure.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
public class RedisConfig {

    // redis-start会自动创建LettuceConnectionFactory ，也可以手动创建JedisConnectionFactory
    @Primary
    @Bean
    public RedisTemplate<String, Object> stringObjectRedisTemplate(
            RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> stringObjectRedisTemplate = new RedisTemplate<>();
        stringObjectRedisTemplate.setConnectionFactory(redisConnectionFactory);
        // 使用FastJson序列化object
        stringObjectRedisTemplate.setDefaultSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        return stringObjectRedisTemplate;
    }
}