package io.github.qifan777.knowledge.graph.chunk;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController("chunk")
@AllArgsConstructor
@Slf4j
public class ChunkController {
    private final ChunkRepository chunkRepository;
    private final Neo4jClient neo4jClient;
    private final EmbeddingModel embeddingModel;

    /**
     * 创建Chunk节点，
     */

    @PostMapping("node")
    public void createNodes() {
        var fileDir = new File("F:\\workspace\\code\\learn\\sec-edgar-notebooks\\data\\sample\\form10k");
        File[] files = fileDir.listFiles();
        for (File file : files) {
            if (!file.getName().contains(".json")) continue;
            chunkRepository.saveAll(fileToChunkList(file));
        }

    }

    /**
     * 解析form10k的中的item属性切割成Chunk
     *
     * @param file form10k的json文件
     * @return Chunk节点
     */
    @SneakyThrows
    public List<Chunk> fileToChunkList(File file) {
        ObjectNode node = new ObjectMapper().readValue(file, ObjectNode.class);
        // 每个form10k有item1，item1a，item7，item7a四种文本信息，都需要将切割
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
                        .text(documents.get(chunkSeqId).getText())
                        .item(item)
                        .build());
            }
        }
        return chunks;
    }

    @PostMapping("link")
    public void createLink() {
        var formIds = neo4jClient
                .query("match (c:Chunk) return distinct c.formId as formId")
                .fetchAs(String.class)
                .all();
        // 每个form10k有item1，item1a，item7，item7a四种文本信息，都需要将切割后的Chunk通过NEXT关联起来
        formIds.forEach(formId -> {
            List.of("item1", "item1a", "item7", "item7a")
                    .forEach(item -> {
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

    /**
     * 对所有Chunk进行embedding，neo4j中支持向量索引，只有创建索引之后才可以查询相似的向量
     */
    @PostMapping("embedding")
    public void createEmbedding() {
        // 随便将一段文本转成向量，看看这个嵌入模型的向量维度是多少
        int dimension = embeddingModel.embed("你好").length;

        // 在Chunk节点创建索引，使用cosine求向量之间的相似度
        neo4jClient.query("""
                        CREATE VECTOR INDEX `form_10k_chunks` IF NOT EXISTS
                        FOR (c:Chunk) ON (c.textEmbedding)
                        OPTIONS { indexConfig: {
                        `vector.dimensions`: $dimensions,
                        `vector.similarity_function`: 'cosine'
                        }}
                        """)
                .bind(dimension).to("dimensions")
                .run();
        // 对那些没有嵌入的Chunk进行embedding
        List<Chunk> waitToEmbedList = chunkRepository.findByTextEmbeddingIsNull();
        waitToEmbedList.forEach(chunk -> chunk.setTextEmbedding(floatsToDoubles(embeddingModel.embed(chunk.getText()))));
        chunkRepository.saveAll(waitToEmbedList);
    }

    public static List<Double> floatsToDoubles(float[] floats) {
        List<Double> result = new ArrayList<>(floats.length);
        for (float f : floats) {
            result.add((double) f);
        }
        return result;
    }

}
