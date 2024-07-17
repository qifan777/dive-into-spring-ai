package io.github.qifan777.knowledge.graph.manager;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Accessors(chain = true)
@Data
@Node
public class Manager {
    @Id
    private String cik;
    private String name;
    private String address;
}
