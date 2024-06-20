package io.github.qifan777.knowledge.ai.message.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiMessageParams {
    Boolean enableVectorStore;
    List<String> functionNames;
}
