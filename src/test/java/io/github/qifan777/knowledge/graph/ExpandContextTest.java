package io.github.qifan777.knowledge.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.qifan.ai.dashscope.DashScopeAiChatModel;
import io.qifan.ai.dashscope.DashScopeAiEmbeddingModel;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.io.File;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ExpandContextTest {
    @Autowired
    FormRepository formRepository;
    @Autowired
    Neo4jClient neo4jClient;
    @Autowired
    DashScopeAiEmbeddingModel embeddingModel;
    @Autowired
    DashScopeAiChatModel chatModel;

    @Test
    public void rag() {
        String query = "Tell me about Netapp's business.";
        List<Double> embed = embeddingModel.embed(query);
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
        Message prompt = new PromptTemplate("""
                Context information is below.
                ---------------------
                {question_answer_context}
                ---------------------
                Given the context and provided history information and not prior knowledge,
                reply to the user comment. If the answer is not in the context, inform
                the user that you can't answer the question.
                """)
                .createMessage(Map.of("question_answer_context", result));
        System.out.println(chatModel.call(new UserMessage(prompt.getContent()+"\n"+query)));
    }

    @Test
    public void connectFormToFirstChunk() {
        neo4jClient.query("""
                match (c:Chunk),(f:Form) where c.chunkSeqId=0 and f.id = c.formId
                merge (f)-[r:SECTION {item:c.item}] -> (c)
                return count(r)
                """);
    }

    @Test
    public void connectChunkToForm() {
        neo4jClient.query("""
                        match (c:Chunk), (f:Form) where c.formId=f.id
                        merge (c)-[r:PART_OF]->(f)
                        return count(r);
                        """)
                .run();
    }

    @Test
    public void readFiles() {
        val fileDir = new File("F:\\workspace\\code\\learn\\sec-edgar-notebooks\\data\\sample\\form10k");
        val files = fileDir.listFiles();
        for (File file : files) {
            if (!file.getName().contains(".json")) continue;
            formRepository.save(fileToForm(file));
        }
    }

    @SneakyThrows
    public Form fileToForm(File file) {
        ObjectNode node = new ObjectMapper().readValue(file, ObjectNode.class);
        var fullText = new StringBuilder("About ")
                .append(node.get("names").asText())
                .append("...");
        String[] items = {"item1", "item1a", "item7", "item7a"};

        for (String item : items) {
            if (node.has(item)) {
                fullText.append(node.get(item).asText())
                        .append("\n");
            }
        }
        String formId = file.getName().replace(".json", "");
        return new Form().setId(formId)
                .setFullText(fullText.toString())
                .setCik(node.get("cik").asText())
                .setCusip6(node.get("cusip6").asText())
                .setSource(node.get("source").asText())
                .setNames(node.get("names").asText());
    }
}
