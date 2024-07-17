package io.github.qifan777.knowledge.graph;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.SyncReadListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.qifan777.knowledge.graph.company.Company;
import io.github.qifan777.knowledge.graph.company.CompanyRepository;
import io.github.qifan777.knowledge.graph.manager.Manager;
import io.github.qifan777.knowledge.graph.manager.ManagerRepository;
import io.github.qifan777.knowledge.graph.model.Form13;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.io.File;
import java.util.List;
import java.util.Map;

@SpringBootTest
@Slf4j
public class CompanyAndInvestContextTest {
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    private Neo4jClient neo4jClient;
    @Autowired
    private ManagerRepository managerRepository;
    @Autowired
    ObjectMapper objectMapper;

    public void rag() {
        neo4jClient.query("""
                CALL db.index.vector.queryNodes('form_10k_chunks', 1, $embedding)
                YIELD node, score
                MATCH (node)-[:PART_OF]->(f:Form),
                    (f)<-[:FILED]-(com:Company),
                    (com)<-[owns:OWNS_STOCK_IN]-(mgr:Manager)
                WITH node, score, mgr, owns, com
                    ORDER BY owns.shares DESC LIMIT 10
                WITH collect (
                    mgr.name +
                    " owns " + owns.shares + " of " + com.name +
                    " at a value of $" + apoc.number.format(owns.value) + "."
                ) AS investment_statements, node, score
                RETURN apoc.text.join(investment_statements, "\\n") +\s
                    "\\n" + node.text AS text
                """);
    }

    public List<Form13> readCSV() {
        SyncReadListener syncReadListener = new SyncReadListener();
        EasyExcel.read(new File("F:\\workspace\\code\\learn\\sec-edgar-notebooks\\data\\sample\\form13.csv"), Form13.class, syncReadListener)
                .sheet()
                .doRead();
        return syncReadListener.getList()
                .stream()
                .map(o -> (Form13) o)
                .toList();
    }

    @Test

    public void createCompanyNodes() {
        List<Form13> form13List = readCSV();
        // form13中一个公司不同证券可能被多个manager投资，导致会有多行重复，先根据cusip id去重一下。
        List<String> cusip6List = form13List.stream().map(Form13::getCusip6)
                .distinct().toList();
        // 每个cusip id映射成Company节点
        List<Company> companies = cusip6List.stream().map(cusip6 -> {

            List<Form13> list = form13List.stream().filter(form13 -> {
                        return form13.getCusip6().equals(cusip6);
                    })
                    .toList();
            // 公司可能有多个证券
            List<String> cusipList = list.stream().map(Form13::getCusip).distinct().toList();
            // 公司的名称可能有多个
            List<String> names = list.stream().map(Form13::getCompanyName).distinct().toList();

            return Company.builder()
                    .names(names)
                    .name(names.get(0))
                    .build();
        }).toList();


        companyRepository.saveAll(companies);
        // 创建公司和form关系
        neo4jClient.query("""
                        match (com:Company), (f:Form) where com.cusip6 = f.cusip6
                        merge (com)-[:FILED]->(form)
                        """)
                .run();
    }

    @Test
    public void createManagerNodes() {
        List<Form13> form13List = readCSV();
        // 投资方可能投资了多个证券，所以会有重复的投资方记录，去重一下
        List<String> cikList = form13List.stream().map(Form13::getManagerCik).distinct().toList();
        // 每个cik代表一个投资方，映射成Manager对象
        List<Manager> managerList = cikList.stream().map(cik -> {
            Form13 manager = form13List.stream()
                    .filter(form13 -> form13.getManagerCik().equals(cik))
                    .findFirst()
                    .orElseThrow(() -> new BusinessException("投资公司不存在"));
            return new Manager()
                    .setCik(manager.getManagerCik())
                    .setName(manager.getManagerName())
                    .setAddress(manager.getManagerAddress());
        }).toList();
        managerRepository.saveAll(managerList);
        // 建立投资人和公司的关系，多次投资同一家公司之选其中一次

        form13List.forEach(form13 -> {

            neo4jClient.query("""
                            match (m:Manager {cik: $managerCik}), (com:Company {cusip6: $cusip6})
                            merge (m)-[owns:OWNS_STOCK_IN {reportCalendarOrQuarter: $reportCalendarOrQuarter}]->(com)
                            on create set
                                owns.value = $value,
                                owns.shares = $shares
                            """)
                    .bindAll(toMap(form13))
                    .run();
        });

    }

    @SneakyThrows
    public Map<String, Object> toMap(Form13 form13) {

        return objectMapper.readValue(objectMapper.writeValueAsString(form13), new TypeReference<Map<String, Object>>() {
        });
    }
}
