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
public class ClassNode {
    @Id
    private String id;
    private String name;
    private String content;
    @Relationship(direction = Relationship.Direction.OUTGOING, type = "OWNS")
    private List<MethodNode> ownsMethodNodes;
    @Relationship(direction = Relationship.Direction.OUTGOING, type = "IMPORTS")
    private List<ClassNode> importNodes;
}
