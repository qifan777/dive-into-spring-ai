package io.github.qifan777.knowledge.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.qifan777.knowledge.graph.form.Form;
import io.github.qifan777.knowledge.graph.form.FormRepository;
import io.github.qifan777.knowledge.graph.model.Form10K;
import io.qifan.ai.dashscope.DashScopeAiChatModel;
import io.qifan.ai.dashscope.DashScopeAiEmbeddingModel;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.moonshot.MoonshotChatModel;
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
    MoonshotChatModel chatModel;

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
        System.out.println(chatModel.call(new UserMessage(prompt.getContent() + "\n" + query)));
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
        var fileDir = new File("F:\\workspace\\code\\learn\\sec-edgar-notebooks\\data\\sample\\form10k");
        var files = fileDir.listFiles();
        for (File file : files) {
            if (!file.getName().contains(".json")) continue;
            formRepository.save(fileToForm(file));
        }
    }

    @SneakyThrows
    public Form fileToForm(File file) {
        var form10K = new ObjectMapper().readValue(file, Form10K.class);
        var fullText = new StringBuilder("About ")
                .append(String.join(",", form10K.getNames()))
                .append("...")
                .append(form10K.getItem1())
                .append("\n")
                .append(form10K.getItem1a())
                .append("\n")
                .append(form10K.getItem7())
                .append("\n")
                .append(form10K.getItem7a());
        var formId = file.getName().replace(".json", "");
        return Form.builder().id(formId)
                .fullText(fullText.toString())
                .cusip6(form10K.getCusip6())
                .source(form10K.getSource())
                .build();
    }
}
