package io.github.qifan777.knowledge.infrastructure.code;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = "code-assistant")
@Data
public class CodeAssistantProperties {
    @NestedConfigurationProperty
    private ProjectProperties project;
    @NestedConfigurationProperty
    private ArthasProperties arthas;

    @Data
    public static class ProjectProperties {
        private String projectPath;
    }

    @Data
    public static class ArthasProperties {
        private String url;
        private String password;
        private String username;
    }
}
