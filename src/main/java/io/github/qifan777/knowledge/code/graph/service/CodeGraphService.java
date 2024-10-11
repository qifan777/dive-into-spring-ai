package io.github.qifan777.knowledge.code.graph.service;

import io.github.qifan777.knowledge.code.graph.entity.MethodNode;
import io.github.qifan777.knowledge.code.graph.repository.ClassNodeRepository;
import io.github.qifan777.knowledge.code.graph.repository.MethodNodeRepository;
import io.github.qifan777.knowledge.infrastructure.code.CodeAssistantProperties;
import io.github.qifan777.knowledge.infrastructure.code.JavaParserUtils;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class CodeGraphService {
    private final ClassNodeRepository classNodeRepository;
    private final MethodNodeRepository methodNodeRepository;
    private final Neo4jClient neo4jClient;
    private final CodeAssistantProperties properties;

    @SneakyThrows
    public void buildGraph() {
        methodNodeRepository.deleteAll();
        classNodeRepository.deleteAll();
        CodeGraphBuilder.BuildContext buildContext = new CodeGraphBuilder(properties.getProject().getProjectPath(), JavaParserUtils.getJavaParser()).buildGraph();
        classNodeRepository.saveAll(buildContext.classNodes());
        log.info("类节点保存完毕: {}", classNodeRepository.count());
        methodNodeRepository.saveAll(buildContext.methodNodes());
        log.info("方法节点保存完毕: {}", methodNodeRepository.count());
    }

    public List<MethodNode> findChildMethods(String methodId) {
        String cypher = """
                match window=(m)-[:USES*0..3]->(:MethodNode)
                where m.id = $methodId
                with nodes(window) as nodeList
                unwind nodeList as nodeRows
                return nodeRows;
                """;
        return findMethods(cypher, methodId);
    }

    public ArrayList<MethodNode> findMethods(String cypher, String methodId) {
        return new ArrayList<>(neo4jClient.query(cypher)
                .bind(methodId).to("methodId")
                .fetchAs(MethodNode.class)
                .mappedBy((typeSystem, record) -> {
                    MethodNode methodNode = new MethodNode();
                    methodNode.setContent(String.valueOf(record.get(0).get("content")));
                    methodNode.setId(String.valueOf(record.get(0).get("id")).replaceAll("\"", ""));
                    return methodNode;
                })
                .all());
    }
}
