package io.github.qifan777.knowledge.infrastructure.code;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Slf4j
@Configuration

public class JavaParserConfiguration {
    @Bean
    public CombinedTypeSolver combinedTypeSolver(CodeAssistantProperties properties) {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new JavaParserTypeSolver(Path.of(properties.getProject().getProjectPath()).resolve(Path.of("src", "main", "java"))));
        return combinedTypeSolver;
    }

    @Bean
    public JavaParser javaParser(CombinedTypeSolver combinedTypeSolver) {
        ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
        return new JavaParser(parserConfiguration);
    }

}
