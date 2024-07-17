package io.github.qifan777.knowledge.graph.chunk;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Builder
@Data
@Node
public class Chunk {
    @Id
    private String id;
    // 切割后的文本
    private String text;
    // item1, item1a, item7, item7a
    private String item;
    // Chunk序列号
    private Integer chunkSeqId;
    // 属于的Form
    private String formId;
    // text的embedding
    private List<Double> textEmbedding;
}
