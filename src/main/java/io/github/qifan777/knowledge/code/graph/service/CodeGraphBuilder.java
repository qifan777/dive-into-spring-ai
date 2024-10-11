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
public class CodeGraphBuilder {
    private final Map<String, String> mapperSqlMap = new HashMap<>();
    private final Map<String, ClassOrInterfaceDeclaration> classDeclarationMap = new HashMap<>();
    private final Map<String, ClassNode> classNodeMap = new HashMap<>();
    private final Map<String, MethodNode> methodNodeMap = new HashMap<>();
    private final Path projectPath;
    private final JavaParser javaParser;

    public CodeGraphBuilder(String projectPath, JavaParser javaParser) {
        this.projectPath = Path.of(projectPath);
        this.javaParser = javaParser;
    }

    public record BuildContext(Collection<ClassNode> classNodes, Collection<MethodNode> methodNodes) {
    }

    /**
     * 构建java方法关系图谱，包含类节点和类方法节点，类和类之间的引用关系，类和方法之间的归属关系，方法和方法之间的调用关系
     *
     * @return 构建好的类节点和类方法节点
     */
    @SneakyThrows
    public BuildContext buildGraph() {
        // 构建mybatis sql
        buildMapperSqlMap();
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

    /**
     * 获取指定路径下的java文件中的所有类声明，一个java文件中可能包含多个类
     *
     * @param path java文件路径
     * @return 类声明列表
     */
    @SneakyThrows
    private List<ClassOrInterfaceDeclaration> getClassDeclarations(Path path) {
        return javaParser.parse(path.toFile())
                .getResult()
                .map(compilationUnit -> compilationUnit.findAll(ClassOrInterfaceDeclaration.class))
                .filter(list -> !list.isEmpty())
                .orElse(List.of());
    }

    /**
     * 递归构建类节点
     *
     * @param declaration 类或者接口声明
     * @return 类节点
     */
    @SneakyThrows
    private Optional<ClassNode> buildClassNode(ClassOrInterfaceDeclaration declaration) {
        // 用classNodeMap缓存，避免重复构建
        if (classNodeMap.containsKey(declaration.getNameAsString())) {
            return Optional.of(classNodeMap.get(declaration.getNameAsString()));
        }
        return declaration
                .getFullyQualifiedName()
                .map(qualifiedClasName -> {
                    ClassNode classNode = new ClassNode().setId(qualifiedClasName)
                            .setName(declaration.getNameAsString())
                            .setContent(declaration.toString());
                    // 缓存类节点
                    classNodeMap.put(qualifiedClasName, classNode);
                    List<ClassNode> importClassNodes = declaration
                            .findAll(ImportDeclaration.class)
                            .stream()
                            // 递归构建类节点
                            .map(importDeclaration -> Optional.ofNullable(classDeclarationMap.get(importDeclaration.getNameAsString()))
                                    .flatMap(this::buildClassNode))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
                    classNode.setImportNodes(importClassNodes);
                    return classNode;
                });
    }

    /**
     * 递归构建方法节点
     *
     * @param methodName   方法名称，如login
     * @param className    方法所在的全限定类名，如io.qifan.xxx.UserService
     * @param declarations 类中的所有方法，如login、logout等
     * @return 方法节点
     */
    @SneakyThrows
    private Optional<MethodNode> buildMethodNode(String methodName, String className, List<MethodDeclaration> declarations) {
        String methodId = className + "#" + methodName;
        // 用methodNodeMap缓存，避免重复构建
        if (methodNodeMap.containsKey(methodId)) {
            return Optional.of(methodNodeMap.get(methodId));
        }
        return declarations.stream()
                .filter(methodDeclaration -> methodDeclaration.getNameAsString().equals(methodName))
                .findFirst()
                .map(methodDeclaration -> {
                    // 获取方法内容，如果是mapper接口则获取方法对应的sql
                    String content = methodDeclaration.findAll(AnnotationExpr.class)
                            .stream()
                            .filter(a -> a.getMetaModel().getQualifiedClassName().equals("org.apache.ibatis.annotations.Mapper"))
                            .findAny()
                            .map(annotationExpr -> mapperSqlMap.get(methodId))
                            .orElse(methodDeclaration.toString());
                    MethodNode methodNode = new MethodNode()
                            .setId(methodId)
                            .setName(methodDeclaration.getNameAsString())
                            .setContent(content)
                            .setComment(methodDeclaration.getComment().map(Comment::getContent).orElse(""));
                    // 缓存方法节点
                    methodNodeMap.put(methodNode.getId(), methodNode);
                    // 递归构建方法调用关系
                    List<MethodNode> usesMethodNodes = methodDeclaration
                            .findAll(MethodCallExpr.class)
                            .stream()
                            .map(this::buildMethodNodeFromMethodCall)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList();
                    methodNode.setUsesMethodNodes(usesMethodNodes);
                    return methodNode;
                });
    }

    /**
     * userService.login()这段代码指的是methodCall，要获取login()的方法体内容，需要先解析userService中的所有方法，然后取出login方法
     *
     * @param methodCallExpr 方法调用表达式
     * @return 方法节点
     */
    public Optional<MethodNode> buildMethodNodeFromMethodCall(MethodCallExpr methodCallExpr) {
        return methodCallExpr
                .getScope()
                .filter(this::checkScopeExist)
                .flatMap(scope -> buildMethodNode(methodCallExpr.getNameAsString(), scope.calculateResolvedType().asReferenceType().getQualifiedName(), getMethodDeclarationsFromScope(scope)));
    }

    /**
     * userService.login(), scope指的是userService. 获取userService中的所有方法
     *
     * @param scope 方法所在的对象
     * @return 方法列表
     */
    public List<MethodDeclaration> getMethodDeclarationsFromScope(Expression scope) {
        return Optional.ofNullable(classDeclarationMap.get(scope.calculateResolvedType()
                        .asReferenceType().getQualifiedName()))
                .map(declaration -> declaration.findAll(MethodDeclaration.class))
                .orElse(List.of());
    }

    /**
     * 初始化mybatis mapper xml，提取其中的sql将方法名称和sql语句对应起来
     */
    @SneakyThrows
    private void buildMapperSqlMap() {
        try (Stream<Path> mapper = Files.walk(getFileInResource("mapper"))) {
            mapper.filter(path -> path.toString().endsWith(".xml"))
                    .forEach(file -> {
                        Document document = parseXMLFileAsDocument(file.toFile());
                        NodeList selectNodes = document.getDocumentElement().getElementsByTagName("select");
                        String namespace = document.getDocumentElement().getAttribute("namespace");
                        extractSqlFromStatement(selectNodes, namespace);
                        NodeList deleteNodes = document.getDocumentElement().getElementsByTagName("delete");
                        extractSqlFromStatement(deleteNodes, namespace);
                        NodeList updateNodes = document.getDocumentElement().getElementsByTagName("update");
                        extractSqlFromStatement(updateNodes, namespace);
                    });
        } catch (Exception ignored) {
            log.warn("不存在mapper");
        }
    }

    /**
     * 解析mapper xml文件
     *
     * @param file mapper文件路径
     * @return document
     */
    @SneakyThrows
    private Document parseXMLFileAsDocument(File file) {
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        return builder.parse(file);
    }

    /**
     * 将mapper中的sql语句提取出来，并和sql语句对应的方法名对应起来
     *
     * @param nodeList  select/delete/update标签
     * @param namespace mapper的namespace
     */
    @SneakyThrows
    private void extractSqlFromStatement(NodeList nodeList, String namespace) {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        for (int i = 0; i < nodeList.getLength(); i++) {
            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(nodeList.item(i)), new StreamResult(writer));
            String output = writer.getBuffer().toString().replaceAll("\n|\r", "");
            mapperSqlMap.put(namespace + "#" + nodeList.item(i).getAttributes().getNamedItem("id").getNodeValue(), output);
        }
    }


    private Path getJavaSourcePath() {
        return projectPath.resolve(Path.of("src", "main", "java"));
    }

    private Path getFileInResource(String fileName) {
        return projectPath.resolve(Path.of("src", "main", "resources", fileName));
    }


    private boolean checkScopeExist(Expression expression) {
        try {
            expression.calculateResolvedType().asReferenceType();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

}
