package io.github.qifan777.knowledge.graph.company;

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
@RequestMapping("company")
@Slf4j
@AllArgsConstructor
public class CompanyController {
    private final CompanyRepository companyRepository;
    private final Neo4jClient neo4jClient;

    @SneakyThrows
    @PostMapping("nodes")
    public void createNodes() {
        var fileDir = new File("F:\\workspace\\code\\learn\\sec-edgar-notebooks\\data\\sample\\form10k");
        File[] files = fileDir.listFiles();
        for (File file : files) {
            if (!file.getName().contains(".json")) continue;
            var form10K = new ObjectMapper().readValue(file, Form10K.class);
            var company = Company.builder().cusip6(form10K.getCusip6())
                    .cusips(form10K.getCusip())
                    .names(form10K.getNames())
                    .name(form10K.getNames().get(0))
                    .build();
            companyRepository.save(company);
        }

    }

    @PostMapping("relationship/filed")
    public void createFiledRelationship() {
        // 创建公司和form关系
        neo4jClient.query("""
                        MATCH (com:Company), (form:Form)
                          WHERE com.cusip6 = form.cusip6
                        MERGE (com)-[:FILED]->(form)
                        """)
                .run();
    }
}
