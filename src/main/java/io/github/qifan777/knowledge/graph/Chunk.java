package io.github.qifan777.knowledge.graph;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Builder
@Accessors(chain = true)
@Data
@Node
public class Chunk {
    @Id
    private String id;
    private String text;
    private String item;
    private Integer chunkSeqId;
    private String formId;
    private List<Double> textEmbedding;
    private String names;
    private String cik;
    private String cusip6;
    private String source;

}
