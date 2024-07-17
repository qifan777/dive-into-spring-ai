package io.github.qifan777.knowledge.graph.form;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Builder
@Data
@Node
public class Form {
    @Id
    private String id;
    private String cusip6;
    private String source;
    private String fullText;
}
