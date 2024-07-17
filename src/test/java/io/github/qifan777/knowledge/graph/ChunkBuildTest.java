package io.github.qifan777.knowledge.graph;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.github.qifan777.knowledge.graph.chunk.Chunk;
import io.github.qifan777.knowledge.graph.chunk.ChunkRepository;
import io.qifan.ai.dashscope.DashScopeAiEmbeddingModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.Neo4jVectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@SpringBootTest
public class ChunkBuildTest {

    @Autowired
    ChunkRepository chunkRepository;
    @Autowired
    Neo4jClient neo4jClient;
    @Autowired
    DashScopeAiEmbeddingModel embeddingModel;
    @Autowired
    Neo4jVectorStore vectorStore;

    @Test
    @SneakyThrows
    public void createChunk() {
        val fileDir = new File("F:\\workspace\\code\\learn\\sec-edgar-notebooks\\data\\sample\\form10k");
        val files = fileDir.listFiles();
        for (File file : files) {
            if (!file.getName().contains(".json")) continue;
            chunkRepository.saveAll(fileToChunkList(file));
        }
    }

    @SneakyThrows
    public List<Chunk> fileToChunkList(File file) {
        ObjectNode node = new ObjectMapper().readValue(file, ObjectNode.class);
        String[] items = {"item1", "item1a", "item7", "item7a"};
        List<Chunk> chunks = new ArrayList<>();
        for (String item : items) {
            String text = node.get(item).asText();
            // 切割文本成
            List<Document> documents = new TokenTextSplitter().split(new Document(text));
            // 最多不超过20 Chunk
            for (int chunkSeqId = 0; chunkSeqId < Integer.min(documents.size(), 20); chunkSeqId++) {
                String formId = file.getName().replace(".json", "");
                chunks.add(Chunk.builder()
                        .id("%s-%s-chunk%04d".formatted(formId, item, chunkSeqId))
                        .chunkSeqId(chunkSeqId)
                        .formId(formId)
                        .text(documents.get(chunkSeqId).getContent())
                        .item(item)
                        .build());
            }
        }
        return chunks;
    }

    @Test
    public void rag() {
        List<Document> documents = vectorStore.similaritySearch("Tell me about Netapp's business.");
        log.info("搜索结果");
    }

    @Test
    public void createLink() {
        Collection<String> formIds = neo4jClient.query("match (c:Chunk) return distinct c.formId as formId")
                .fetchAs(String.class).all();
        formIds.forEach(formId -> {
            List.of("item1", "item1a", "item7", "item7a").forEach(item -> {
                neo4jClient.query("""
                                MATCH (c:Chunk) // 匹配所有的节点
                                WHERE c.formId = $formId // 属于同一个form和同一个item的节点
                                  AND c.item = $item
                                WITH c
                                  ORDER BY c.chunkSeqId ASC // 根据seqId排序一下节点
                                WITH collect(c) as section_chunk_list // 转成list
                                  CALL apoc.nodes.link(section_chunk_list, "NEXT", {avoidDuplicates: true}) // 节点之间依按顺序创建连接
                                RETURN size(section_chunk_list)
                                """)
                        .bind(formId).to("formId")
                        .bind(item).to("item")
                        .run();
            });
        });
    }

    @Test
    public void createEmbedding() {
        String cypher = """
                CREATE VECTOR INDEX `form_10k_chunks` IF NOT EXISTS
                FOR (c:Chunk) ON (c.textEmbedding)
                OPTIONS { indexConfig: {
                `vector.dimensions`: $dimensions,
                `vector.similarity_function`: 'cosine'
                }}
                """;
        ;

        int dimension = embeddingModel.embed("你好").size();
        log.info("维度：{}", dimension);
        neo4jClient.query(cypher)
                .bind(dimension).to("dimensions")
                .run();
        List<Chunk> waitToEmbedList = chunkRepository.findByTextEmbeddingIsNull();
        waitToEmbedList.forEach(chunk -> {
            chunk.setTextEmbedding(embeddingModel.embed(chunk.getText()));
        });
        chunkRepository.saveAll(waitToEmbedList);
        log.info("{}", waitToEmbedList.size());
    }


}