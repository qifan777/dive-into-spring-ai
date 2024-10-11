package io.github.qifan777.knowledge.infrastructure.code;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@Slf4j
public class JavaParserUtils implements ApplicationContextAware {
    private static CodeAssistantProperties properties;

    public static JavaParser getJavaParser() {
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new JavaParserTypeSolver(Path.of(properties.getProject().getProjectPath()).resolve(Path.of("src", "main", "java"))));
        ParserConfiguration parserConfiguration = new ParserConfiguration().setSymbolResolver(new JavaSymbolSolver(combinedTypeSolver));
        return new JavaParser(parserConfiguration);
    }

    @SneakyThrows
    public static ParseResult<CompilationUnit> parse(Path path) {
        return getJavaParser().parse(path);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        properties = applicationContext.getBean(CodeAssistantProperties.class);
    }
}
