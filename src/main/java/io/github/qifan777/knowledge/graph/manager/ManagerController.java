package io.github.qifan777.knowledge.graph.manager;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.SyncReadListener;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.qifan777.knowledge.graph.model.Form13;
import io.qifan.infrastructure.common.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("manager")
@AllArgsConstructor
@Slf4j
public class ManagerController {


    private final Neo4jClient neo4jClient;
    private final ObjectMapper jacksonObjectMapper;
    private final ManagerRepository managerRepository;

    public List<Form13> readForm13List() {
        SyncReadListener syncReadListener = new SyncReadListener();
        EasyExcel.read(new File("F:\\workspace\\code\\learn\\sec-edgar-notebooks\\data\\sample\\form13.csv"), Form13.class, syncReadListener)
                .sheet()
                .doRead();
        return syncReadListener.getList()
                .stream()
                .map(o -> (Form13) o)
                .toList();
    }

    @PostMapping("nodes")
    public void createNodes() {
        List<Form13> form13List = readForm13List();
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
    }

    @PostMapping("relationship/stock-in")
    public void createStockInRelationship() {
        List<Form13> form13List = readForm13List();
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
        return jacksonObjectMapper.readValue(jacksonObjectMapper.writeValueAsString(form13), new TypeReference<>() {
        });
    }
}
