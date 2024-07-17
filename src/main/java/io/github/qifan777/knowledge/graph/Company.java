package io.github.qifan777.knowledge.graph;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

import java.util.List;

@Accessors(chain = true)
@Data
@Node
public class Company {
    @Id
    private String cusip6;
    private List<String> cusips;
    private List<String> names;
    private String name;
}
