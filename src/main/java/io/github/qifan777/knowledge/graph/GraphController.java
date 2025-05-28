package io.github.qifan777.knowledge.graph;

import io.github.qifan777.knowledge.graph.chunk.ChunkController;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController("graph")
@AllArgsConstructor
@Slf4j
public class GraphController {
    private final EmbeddingModel embeddingModel;
    private final ChatModel chatModel;
    private final Neo4jClient neo4jClient;
    private final PromptTemplate promptTemplate = new PromptTemplate("""
            Context information is below.
            ---------------------
            {question_answer_context}
            ---------------------
            Given the context and provided history information and not prior knowledge,
            reply to the user comment. If the answer is not in the context, inform
            the user that you can't answer the question.
            """);

    @GetMapping(value = "chunk/rag")
    public String chunkRag(@RequestParam String query) {
        List<Double> embed = ChunkController.floatsToDoubles(embeddingModel.embed(query));
        String result = neo4jClient.query("""
                        CALL db.index.vector.queryNodes('form_10k_chunks', 1, $embedding)
                        yield node, score
                        match window=(:Chunk)-[:NEXT*0..1]->(node)-[:NEXT*0..1]->(:Chunk)
                        with nodes(window) as chunkList, node, score
                        unwind chunkList as chunkRows
                        with collect(chunkRows.text) as textList, node, score
                        return apoc.text.join(textList, " \\n ")
                        """)
                .bind(embed).to("embedding")
                .fetchAs(String.class).first()
                .orElseThrow(() -> new BusinessException("未找到相似文档"));
        String content = promptTemplate.createMessage(Map.of("question_answer_context", result)).getText();
        return chatModel.call(new UserMessage(content + "\n" + query));
    }

    @GetMapping(value = "manager/rag")
    public String managerRag(@RequestParam String query) {
        List<Double> embed = ChunkController.floatsToDoubles(embeddingModel.embed(query));
        var result = neo4jClient.query("""
                        CALL db.index.vector.queryNodes('form_10k_chunks', 1, $embedding)
                        YIELD node, score
                        MATCH (node)-[:PART_OF]->(f:Form),
                            (f)<-[:FILED]-(com:Company),
                            (com)<-[owns:OWNS_STOCK_IN]-(mgr:Manager)
                        WITH node, score, mgr, owns, com
                            ORDER BY owns.shares DESC LIMIT 5
                        WITH collect (
                            mgr.name +
                            " owns " + owns.shares + " of " + com.name +
                            " at a value of $" + apoc.number.format(owns.value) + "."
                        ) AS investment_statements, node, score
                        RETURN "investors: \\n" + apoc.text.join(investment_statements, "\\n") +\s
                            "\\n" + node.text AS text
                        """)
                .bind(embed).to("embedding")
                .fetchAs(String.class)
                .first()
                .orElseThrow(() -> new BusinessException("未找到相似文档"));
        String content = promptTemplate.createMessage(Map.of("question_answer_context", result)).getText();
        log.info("context result: {}", content);
        return chatModel.call(new UserMessage(content + "\n" + query));
    }
}
