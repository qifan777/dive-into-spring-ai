package io.github.qifan777.knowledge.graph;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Accessors(chain = true)
@Data
@Node
public class Form {
    @Id
    String id;
    String names;
    String cik;
    String cusip6;
    String source;
    String fullText;
}
