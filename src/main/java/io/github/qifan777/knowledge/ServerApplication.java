package io.github.qifan777.knowledge;

import io.github.qifan777.knowledge.infrastructure.code.CodeAssistantProperties;
import org.babyfish.jimmer.client.EnableImplicitApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableImplicitApi
@EnableConfigurationProperties(CodeAssistantProperties.class)
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
    }
}
