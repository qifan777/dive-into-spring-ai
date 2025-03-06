package io.github.qifan777.knowledge.ai.message.dto;

import io.github.qifan777.knowledge.ai.message.AiMessage;
import lombok.Data;

@Data
public class AiMessageWrapper {
    AiMessage message;
    AiMessageParams params;
}
