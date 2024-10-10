package io.github.qifan777.knowledge.code.graph.entity;

import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
@Data
@Accessors(chain = true)
public class MethodNode {
    @Id
    private String id;
    private String name;
    private String comment;
    private String content;
    @Relationship(direction = Relationship.Direction.OUTGOING, type = "USES")
    private List<MethodNode> usesMethodNodes;
}
