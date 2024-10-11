package io.github.qifan777.knowledge.code.graph.controller;

import io.github.qifan777.knowledge.code.graph.service.CodeGraphService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("code/graph")
@AllArgsConstructor
@Slf4j
public class CodeGraphController {
    private final CodeGraphService codeGraphService;

    @PostMapping("build")
    public String buildGraph() {
        codeGraphService.buildGraph();
        return "SUCCESS";
    }
}
