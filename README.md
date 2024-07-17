# 项目介绍

![项目内容](./front-end/src/assets/cover.png)

![知识点](./front-end/src/assets/SrpingAI知识点.png)

SpringAI教程包含Agent（智能体）/SSE流/FunctionCall/Embedding/VectorDatabase/RAG/历史消息/图片理解/等知识点。

[文档地址](https://www.jarcheng.top/blog/project/spring-ai/intro.html)
[视频地址](https://www.bilibili.com/video/BV14y411q7RN/)

## 运行环境

- Java 17
- Node.js 18+
- MySQL 8
- Redis-Stack
- DashScope API KEY（或者其他）

## 运行步骤

### 1.clone代码

```shell
git clone https://github.com/qifan777/KnowledgeBaseChatSpringAI
```

### 2. idea打开项目

### 3. 修改配置文件

修改application.yml中的API-KEY, MySQL, Redis-Stack配置

### 4. 运行项目

后端运行

1. 运行ServerApplication.java
2. target/generated-sources/annotations右键mark directory as/generated source root

前端运行，在front-end目录下

- npm run install
- npm run api （先运行后端）
- npm run dev

```shell
docker run \
    -d \
    -p 7474:7474 -p 7687:7687 \
    -v neo4j-data:/data -v neo4j-data:/plugins \
    --name neo4j \
    -e NEO4J_apoc_export_file_enabled=true \
    -e NEO4J_apoc_import_file_enabled=true \
    -e NEO4J_apoc_import_file_use__neo4j__config=true \
    -e NEO4JLABS_PLUGINS=\[\"apoc\"\] \
    -e NEO4J_dbms_security_procedures_unrestricted=apoc.\\\* \
    neo4j
```

## 联系方式

付费远程运行/安装/定制开发联系微信：ljc666max

其他关于程序运行安装报错请加QQ群：416765656
