package io.github.qifan777.knowledge.ai.message.dto;

import lombok.Data;

@Data
public class AiMessageWrapper {
    AiMessageInput message;
    AiMessageParams params;
}
