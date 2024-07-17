package io.github.qifan777.knowledge.graph.form;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.qifan777.knowledge.graph.model.Form10K;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("form")
@AllArgsConstructor
@Slf4j
public class FormController {
    private final FormRepository formRepository;
    private final Neo4jClient neo4jClient;

    @SneakyThrows
    @PostMapping("node")
    public void createNodes() {
        var fileDir = new File("F:\\workspace\\code\\learn\\sec-edgar-notebooks\\data\\sample\\form10k");
        File[] files = fileDir.listFiles();
        for (File file : files) {
            if (!file.getName().contains(".json")) continue;
            var form10K = new ObjectMapper().readValue(file, Form10K.class);
            var fullText = "About " +
                           String.join(",", form10K.getNames()) +
                           "..." +
                           form10K.getItem1() +
                           "\n" +
                           form10K.getItem1a() +
                           "\n" +
                           form10K.getItem7() +
                           "\n" +
                           form10K.getItem7a();
            var formId = file.getName().replace(".json", "");
            var form = Form.builder().id(formId)
                    .fullText(fullText)
                    .cusip6(form10K.getCusip6())
                    .source(form10K.getSource())
                    .build();
            formRepository.save(form);
        }
    }

    @PostMapping("relationship/section")
    public void createSectionRelationship() {
        neo4jClient.query("""
                        match (c:Chunk),(f:Form) where c.chunkSeqId=0 and f.id = c.formId
                        merge (f)-[r:SECTION {item:c.item}] -> (c)
                        return count(r)
                        """)
                .run();
    }

    @PostMapping("relationship/part-of")
    public void createPartOfRelationship() {
        neo4jClient.query("""
                        match (c:Chunk), (f:Form) where c.formId=f.id
                        merge (c)-[r:PART_OF]->(f)
                        return count(r);
                        """)
                .run();
    }
}
