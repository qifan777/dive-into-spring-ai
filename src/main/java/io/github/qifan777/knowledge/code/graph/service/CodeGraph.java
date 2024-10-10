package io.github.qifan777.knowledge.code.graph.service;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import io.github.qifan777.knowledge.code.graph.entity.ClassNode;
import io.github.qifan777.knowledge.code.graph.entity.MethodNode;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
public class CodeGraph {
//    private final Map<String, String> mapperSqlMap = new HashMap<>();
    private final Map<String, ClassOrInterfaceDeclaration> classDeclarationMap = new HashMap<>();
    private final Map<String, ClassNode> classNodeMap = new HashMap<>();
    private final Map<String, MethodNode> methodNodeMap = new HashMap<>();
    private final Path projectPath;
    private final JavaParser javaParser;

    public CodeGraph(String projectPath, JavaParser javaParser) {
        this.projectPath = Path.of(projectPath);
        this.javaParser = javaParser;
    }

    public record BuildContext(Collection<ClassNode> classNodes, Collection<MethodNode> methodNodes) {
    }


    @SneakyThrows
    public BuildContext buildGraph() {
//        initMapper();
        try (Stream<Path> pathStream = Files.walk(getJavaSourcePath())) {
            pathStream.filter(path -> path.toFile().isFile())
                    .flatMap(path -> getClassDeclarations(path).stream())
                    .filter(declaration -> declaration.getFullyQualifiedName().isPresent())
                    .forEach(declaration -> classDeclarationMap.put(declaration.getFullyQualifiedName().get(), declaration));
            classDeclarationMap.values().forEach(this::buildClassNode);
            classNodeMap
                    .values()
                    .forEach(classNode -> {
                        ClassOrInterfaceDeclaration classOrInterfaceDeclaration = classDeclarationMap.get(classNode.getId());
                        List<MethodDeclaration> methodDeclarations = classOrInterfaceDeclaration.findAll(MethodDeclaration.class);
                        List<MethodNode> ownsMethodNodes = methodDeclarations
                                .stream()
                                .map(methodDeclaration -> buildMethodNode(methodDeclaration.getNameAsString(), classNode.getId(), methodDeclarations))
                                .filter(Optional::isPresent)
                                .map(Optional::get)
                                .toList();
                        classNode.setOwnsMethodNodes(ownsMethodNodes);
                    });

        }
        return new BuildContext(classNodeMap.values(), methodNodeMap.values());
    }

    @SneakyThrows
    private List<ClassOrInterfaceDeclaration> getClassDeclarations(Path path) {
        return javaParser.parse(path.toFile())
                .getResult()
                .map(compilationUnit -> compilationUnit.findAll(ClassOrInterfaceDeclaration.class))
                .filter(list -> !list.isEmpty())
                .orElse(List.of());
    }

    @SneakyThrows
    private Optional<ClassNode> buildClassNode(ClassOrInterfaceDeclaration declaration) {
        if (classNodeMap.containsKey(declaration.getNameAsString())) {
            return Optional.of(classNodeMap.get(declaration.getNameAsString()));
        }
        return declaration
                .getFullyQualifiedName()
                .map(qualifiedClasName -> {
                    ClassNode classNode = new ClassNode().setId(qualifiedClasName)
                            .setName(declaration.getNameAsString())
                            .setContent(declaration.toString());
                    classNodeMap.put(qualifiedClasName, classNode);
                    List<ClassNode> importClassNodes = declaration
                            .findAll(ImportDeclaration.class)
                            .stream()
                            .map(importDeclaration -> Optional.ofNullable(classDeclarationMap.get(importDeclaration.getNameAsString()))
                                    .flatMap(this::buildClassNode))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
                    classNode.setImportNodes(importClassNodes);
                    return classNode;
                });
    }

    @SneakyThrows
    private Optional<MethodNode> buildMethodNode(String methodName, String className, List<MethodDeclaration> declarations) {
        String methodId = className + "#" + methodName;
        if (methodNodeMap.containsKey(methodId)) {
            return Optional.of(methodNodeMap.get(methodId));
        }
        return declarations.stream()
                .filter(methodDeclaration -> methodDeclaration.getNameAsString().equals(methodName))
                .findFirst()
                .map(methodDeclaration -> {
//                    String content = methodDeclaration.findAll(AnnotationExpr.class)
//                            .stream()
//                            .filter(a -> a.getMetaModel().getQualifiedClassName().equals("org.apache.ibatis.annotations.Mapper"))
//                            .findAny()
//                            .map(annotationExpr -> mapperSqlMap.get(methodId))
//                            .orElse(methodDeclaration.toString());
                    MethodNode methodNode = new MethodNode()
                            .setId(methodId)
                            .setName(methodDeclaration.getNameAsString())
                            .setContent(methodDeclaration.toString())
                            .setComment(methodDeclaration.getComment().map(Comment::getContent).orElse(""));
                    methodNodeMap.put(methodNode.getId(), methodNode);
                    List<MethodNode> usesMethodNodes = methodDeclaration
                            .findAll(MethodCallExpr.class)
                            .stream()
                            .map(this::getMethodNode)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
                    methodNode.setUsesMethodNodes(usesMethodNodes);
                    return methodNode;
                });
    }

    public Optional<MethodNode> getMethodNode(MethodCallExpr methodCallExpr) {
        return methodCallExpr
                .getScope()
                .filter(this::checkScopeExist)
                .flatMap(scope -> buildMethodNode(methodCallExpr.getNameAsString(), scope.calculateResolvedType().asReferenceType().getQualifiedName(), getMethodDeclarations(scope)));
    }

    public List<MethodDeclaration> getMethodDeclarations(Expression scope) {
        return Optional.ofNullable(classDeclarationMap.get(scope.calculateResolvedType()
                        .asReferenceType().getQualifiedName()))
                .map(declaration -> declaration.findAll(MethodDeclaration.class))
                .orElse(List.of());
    }

//    @SneakyThrows
//    private void initMapper() {
//        try (Stream<Path> mapper = Files.walk(getFileInResource("mapper"));) {
//            mapper.filter(file -> file.toString().endsWith(".xml")).forEach(file -> {
//                Document parse = getMapperDocument(file.toFile());
//                NodeList selectNodes = parse.getDocumentElement().getElementsByTagName("select");
//                String namespace = parse.getDocumentElement().getAttribute("namespace");
//                getMapperMethodList(selectNodes, namespace);
//                NodeList deleteNodes = parse.getDocumentElement().getElementsByTagName("delete");
//                getMapperMethodList(deleteNodes, namespace);
//                NodeList updateNodes = parse.getDocumentElement().getElementsByTagName("update");
//                getMapperMethodList(updateNodes, namespace);
//            });
//        }
//    }

//    @SneakyThrows
//    private Document getMapperDocument(File file) {
//        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
//        return builder.parse(file);
//    }
//
//    @SneakyThrows
//    private void getMapperMethodList(NodeList nodeList, String namespace) {
//        TransformerFactory tf = TransformerFactory.newInstance();
//        Transformer transformer = tf.newTransformer();
//        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            StringWriter writer = new StringWriter();
//            transformer.transform(new DOMSource(nodeList.item(i)), new StreamResult(writer));
//            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
//            mapperSqlMap.put(namespace + "#" + nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue(), output);
//        }
//    }


    private Path getJavaSourcePath() {
        return projectPath.resolve(Path.of("src", "main", "java"));
    }

//    private Path getFileInResource(String fileName) {
//        return projectPath.resolve(Path.of("src", "main", "resources", fileName));
//    }


    private boolean checkScopeExist(Expression expression) {
        try {
            expression.calculateResolvedType().asReferenceType();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
