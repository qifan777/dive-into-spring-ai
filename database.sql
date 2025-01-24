-- MySQL dump 10.13  Distrib 8.0.29, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: knowledge_base
-- ------------------------------------------------------
-- Server version	8.0.29

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `ai_document`
--

DROP TABLE IF EXISTS `ai_document`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_document` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) NOT NULL,
  `editor_id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL COMMENT '名称',
  `content` text NOT NULL COMMENT '内容',
  `doc_ids` json NOT NULL COMMENT '片段id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='知识库';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_document`
--

LOCK TABLES `ai_document` WRITE;
/*!40000 ALTER TABLE `ai_document` DISABLE KEYS */;
/*!40000 ALTER TABLE `ai_document` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_factory`
--

DROP TABLE IF EXISTS `ai_factory`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_factory` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) NOT NULL,
  `editor_id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL COMMENT '名称',
  `value` varchar(32) NOT NULL COMMENT '值',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ai厂家';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_factory`
--

LOCK TABLES `ai_factory` WRITE;
/*!40000 ALTER TABLE `ai_factory` DISABLE KEYS */;
INSERT INTO `ai_factory` VALUES ('c4f7d6fb00e249a1be364319ca94bb71','2025-01-03 14:55:23.557359','2025-01-03 14:55:23.557359','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','OpenAI','OPEN_AI');
/*!40000 ALTER TABLE `ai_factory` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_function`
--

DROP TABLE IF EXISTS `ai_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_function` (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `name` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '函数名称',
  `description` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '函数描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI函数';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_function`
--

LOCK TABLES `ai_function` WRITE;
/*!40000 ALTER TABLE `ai_function` DISABLE KEYS */;
INSERT INTO `ai_function` VALUES ('93047e71d4d74d68af5f7549d259fb93','2024-06-21 16:33:59.047526','2024-06-21 16:33:59.047526','documentAnalyzerFunction','文档解析函数');
/*!40000 ALTER TABLE `ai_function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_key`
--

DROP TABLE IF EXISTS `ai_key`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_key` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) NOT NULL,
  `editor_id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL COMMENT '名称',
  `value` varchar(64) NOT NULL COMMENT '值',
  `factory_id` varchar(36) NOT NULL COMMENT '所属厂家',
  `usage_token` decimal(10,2) DEFAULT NULL COMMENT '已使用token',
  `total_token` decimal(10,2) DEFAULT NULL COMMENT '总token',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='api key';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_key`
--

LOCK TABLES `ai_key` WRITE;
/*!40000 ALTER TABLE `ai_key` DISABLE KEYS */;
INSERT INTO `ai_key` VALUES ('3daad7142c08477186020d910a752b0f','2025-01-03 15:02:40.469750','2025-01-03 15:02:40.469750','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','Open AI Key','xxxx','c4f7d6fb00e249a1be364319ca94bb71',0.00,10000000.00);
/*!40000 ALTER TABLE `ai_key` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_message`
--

DROP TABLE IF EXISTS `ai_message`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_message` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) NOT NULL,
  `editor_id` varchar(32) NOT NULL,
  `type` varchar(32) NOT NULL COMMENT '消息类型(用户/助手/系统)',
  `text_content` text NOT NULL COMMENT '消息内容',
  `medias` json DEFAULT NULL COMMENT '媒体内容如图片链接、语音链接',
  `ai_session_id` varchar(32) NOT NULL COMMENT '会话id',
  PRIMARY KEY (`id`),
  KEY `ai_message_ai_session_id_fk` (`ai_session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_message`
--

LOCK TABLES `ai_message` WRITE;
/*!40000 ALTER TABLE `ai_message` DISABLE KEYS */;
INSERT INTO `ai_message` VALUES ('1c16b5ee44254e49a59b3694f4f68c3a','2024-12-20 10:23:50.199907','2024-12-20 10:23:50.199907','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT',' 这份简历的主要内容如下：\n\n个人信息: 林家成，男，2000 年出生。电子邮箱为1507906763@qq.com, 求职意向是Java 研发工程师。\n\n教育经历: 在华侨大学攻读信息管理与信息系统专业学士学位，入学时间为2019.9，预计毕业时间为2023.7。\n\n实习经历: 在叁拾信息科技有限公司担任java开发工程师，工作时间从2021.5到2021.11。主要负责多租户支付小程序项目的后端全部开发和设计，并编写技术文档。\n\n项目经历: 参与了多个项目，包括华大快帮、在线寄信小程序和卖课件项目等。这些项目涉及到的技术有SpringCloud、SpringBoot、Vue3、Docker、MySQL等。在项目中负责的工作涵盖了从后端服务搭建、前端页面开发、数据库设计到容器部署等多个方面。\n\n技能清单: 具备JDK基础、Jakarta EE、多种数据库操作、前后端开发框架（如Spring全家桶、Vue系列）、微服务架构（如Spring Cloud）以及一些运维知识（如Nginx、Docker、Linux）。同时对Redis、RabbitMq、ElasticSearch中间件和计算机基础（如数据库、计算机网络、操作系统、数据结构与算法）也有一定了解。','[]','b495186b9259494792a9fe4ce8807f25'),('1d14d513ae8c4ffda0fb50df6e24ba77','2024-06-20 22:08:28.389711','2024-06-20 22:08:28.389711','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','C:\\Users\\Administrator\\Desktop\\2023年工作总结及2024年工作展望-林家成.docx，这份文档的内容是什么？','[]','c4d192c4b2de48bcae585f5b9672cd1d'),('25f95a91895a432fa944259f8a5f41b2','2024-12-20 10:22:08.541262','2024-12-20 10:22:08.541262','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT',' 在C:\\Users\\a1507\\Desktop\\gitea目录下有三个文件/文件夹: git, gitea, ssh。','[]','b495186b9259494792a9fe4ce8807f25'),('2b780c6a4dc744e6871525196fb6ae43','2024-10-10 10:41:04.313536','2024-10-10 10:41:04.313536','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','请你诊断一下这个方法出现异常的原因是什么io.github.qifan777.knowledge.user.UserController#login','[]','dcd32fd9f7fc4d0fb9a5b7dc9ff0dbc8'),('43fcd2c8478a4fb49ceaad708fa0e6e4','2024-09-18 10:00:18.933479','2024-09-18 10:00:18.933479','0f07d638f1bc401188d86dc650ab06a7','0f07d638f1bc401188d86dc650ab06a7','ASSISTANT','你好！很高兴能为你提供帮助。如果你有任何问题，尽管问我。','[]','8d09f903b86740e58c5a5ddf5a6d2257'),('6a44dd16f1ab4a72b5ed7bcf87ae4d0f','2024-10-10 10:41:04.519006','2024-10-10 10:41:04.519006','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','在`UserController`类的`login`方法中，抛出了一個名为`BusinessException`的异常，其信息指出原因是\"用户名/密码错误\"。具体而言，这个问题出现在代码的第34行，当尝试使用您提供的手机号码查找用户并对密码进行验证时遇到问题。\n\n根据您的登录参数`{\"password\":\"123456231\",\"phone\":\"13656987994\"}`，异常可能是由以下原因之一引起的：\n- 使用的手机号未在系统中注册过。\n- 输入的密码与该手机号对应的账户密码不匹配。\n\n因此，登录失败的具体原因是**输入的用户名或密码不正确**。请检查并确认所提供的凭证无误。','[]','dcd32fd9f7fc4d0fb9a5b7dc9ff0dbc8'),('751fcc48fa694f8dadb65234992282bf','2024-10-11 15:14:31.222845','2024-10-11 15:14:31.222845','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','请通过下面的链接查看`UserController.java`文件的评审结果：[点击这里](http://localhost:5177/#/analyze?path=io/github/qifan777/knowledge/user/UserController.java)。\n\n在这个链接中，你会看到对于代码的详细分析报告，包括任何潜在的问题或者改进建议。如果链接无法打开，请告知我以便提供进一步的帮助。','[]','dcd32fd9f7fc4d0fb9a5b7dc9ff0dbc8'),('9964ad78f35d498b8743e389b00b2093','2024-09-18 10:00:18.835022','2024-09-18 10:00:18.835022','0f07d638f1bc401188d86dc650ab06a7','0f07d638f1bc401188d86dc650ab06a7','USER','你好','[]','8d09f903b86740e58c5a5ddf5a6d2257'),('9c8f6a3155e047ef8d2b7ab7b3cdb934','2024-10-10 10:38:20.239336','2024-10-10 10:38:20.239336','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','请在下面的网页链接查看`UserController.java`的代码评审结果：\n\n[代码评审结果](http://localhost:5177/#/analyze?path=io/github/qifan777/knowledge/user/UserController.java)\n\n这个链接将会提供关于`login`方法及该文件其他部分的详细分析信息，帮助诊断可能的异常原因。','[]','dcd32fd9f7fc4d0fb9a5b7dc9ff0dbc8'),('ba161500bec84b388a6501b3be333b45','2024-06-20 22:08:28.649439','2024-06-20 22:08:28.649439','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','2023年的工作总结及2024年工作展望文档内容概要如下：\n\n1、**工作回顾重点：**\n   - 成功运用Python脚本在一经上云项目中实现Oracle脚本批量迁移、实体批量登记等，极大提升了工作效率（速度提升70倍以上），并提前完成年度工作任务。\n   - 领导新智慧工厂项目的前端与后端框架搭建，采用更高效、安全的技术栈，促进团队快速投入业务开发。重构并优化了基础功能模块，如菜单框架、租户管理等，封装为易用组件。\n   - 开发了脚本依赖分析工具，通过SQL语法树解析等技术，有效辅助故障检测、代码规范检查及开发效率，与部门其他系统（如指标库、报表系统）集成，增强功能实用性。\n\n2、**个人成长与进步：**\n   - 今年最大的成就是深入学习了数据开发知识，综合软件开发能力使自己更能满足用户需求，相比同行具有更全面的技能组合。\n\n3、**应对挑战的方法：**\n   - 面对SQL语法解析等高难度任务，通过自学源码、复习理论基础，成功开发出工具，体现了自我驱动力和解决问题的能力。\n\n4、**反思与改进空间：**\n   - 希望能在项目中实践深度学习、机器学习技术，这是未来努力的一个方向。\n\n5、**提质增效的建议：**\n   - 强调代码审查和个人技能提升，鼓励使用先进技术和工具，以及创建持续学习的环境，以保持竞争力。\n\n6、**对当前开发工作的建议：**\n   - 提倡技术分享会议，打破知识孤岛，促进新老员工间的技术交流和学习，平衡稳定与创新的需求。\n\n7、**其他建议：**\n   - 加强跨部门合作，以新智慧工厂项目为范例，推动更多跨领域协作成果。\n\n对于2024年的展望虽未详细展开，但基于上述总结，可能包括深化技术应用、促进团队协作、继续个人技能升级等方面。',NULL,'c4d192c4b2de48bcae585f5b9672cd1d'),('c01929b2ef314c01b26fa7ea4739bde5','2024-12-20 10:22:08.245206','2024-12-20 10:22:08.245206','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','帮我读取一下C:\\Users\\a1507\\Desktop\\gitea这个文件夹','[]','b495186b9259494792a9fe4ce8807f25'),('c8d5fad0ecde4262b965766b96672c12','2024-12-20 10:23:49.914725','2024-12-20 10:23:49.914725','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','我的这份简历的主要内容是什么？','[]','b495186b9259494792a9fe4ce8807f25'),('d8448bcceb27487d86e9a1e5e4e215e7','2024-09-28 10:08:53.079413','2024-09-28 10:08:53.079413','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','可以帮助你的吗？','[]','c4d192c4b2de48bcae585f5b9672cd1d'),('e57629773b86436f94fa2dc77c06d86c','2024-09-28 10:08:52.700716','2024-09-28 10:08:52.700716','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','你好','[]','c4d192c4b2de48bcae585f5b9672cd1d'),('e8d9b1b805a24445bed34ea8c18a38a9','2024-10-10 10:38:19.881553','2024-10-10 10:38:19.881553','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','请你诊断一下这个方法出现异常的原因是什么io.github.qifan777.knowledge.user.UserController#login','[]','dcd32fd9f7fc4d0fb9a5b7dc9ff0dbc8'),('e9cbcc0f442146709f027c89ccacb5dd','2024-10-11 15:14:31.185493','2024-10-11 15:14:31.185493','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','请你分析一下这个文件io/github/qifan777/knowledge/user/UserController.java','[]','dcd32fd9f7fc4d0fb9a5b7dc9ff0dbc8');
/*!40000 ALTER TABLE `ai_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_model`
--

DROP TABLE IF EXISTS `ai_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_model` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) NOT NULL,
  `editor_id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL COMMENT '名称',
  `value` varchar(64) NOT NULL COMMENT '值',
  `factory_id` varchar(36) NOT NULL COMMENT '所属厂家',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='ai模型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_model`
--

LOCK TABLES `ai_model` WRITE;
/*!40000 ALTER TABLE `ai_model` DISABLE KEYS */;
INSERT INTO `ai_model` VALUES ('e5c9243efd6a454daa7e7fba2d238c07','2025-01-03 15:03:44.549015','2025-01-03 15:03:44.549015','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','测试模型','gpt4','c4f7d6fb00e249a1be364319ca94bb71');
/*!40000 ALTER TABLE `ai_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_session`
--

DROP TABLE IF EXISTS `ai_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_session` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) NOT NULL,
  `editor_id` varchar(32) NOT NULL,
  `name` varchar(32) NOT NULL COMMENT '会话名称',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_session`
--

LOCK TABLES `ai_session` WRITE;
/*!40000 ALTER TABLE `ai_session` DISABLE KEYS */;
INSERT INTO `ai_session` VALUES ('8d09f903b86740e58c5a5ddf5a6d2257','2024-09-18 09:20:56.059320','2024-09-18 09:20:56.059320','0f07d638f1bc401188d86dc650ab06a7','0f07d638f1bc401188d86dc650ab06a7','新的聊天'),('b495186b9259494792a9fe4ce8807f25','2024-06-18 22:01:51.459293','2024-06-18 22:01:51.459293','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','新的聊天'),('c4d192c4b2de48bcae585f5b9672cd1d','2024-06-18 22:01:45.819825','2024-06-18 22:01:45.819317','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','新的聊天'),('c8bd6033bbe14595aaa2e2c63a969b11','2024-07-18 17:45:28.755705','2024-07-18 17:45:28.755705','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','新的聊天'),('dcd32fd9f7fc4d0fb9a5b7dc9ff0dbc8','2024-06-18 21:57:09.390651','2024-06-18 21:57:09.390651','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','新的聊天');
/*!40000 ALTER TABLE `ai_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `nickname` varchar(20) DEFAULT NULL,
  `avatar` varchar(255) DEFAULT NULL,
  `gender` varchar(36) DEFAULT NULL,
  `phone` varchar(20) NOT NULL,
  `password` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `phone` (`phone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES ('dcd256e2412f4162a6a5fcbd5cfedc84','2024-05-01 16:52:43.364225','2024-05-19 21:30:34.686818','起凡','https://my-community.oss-cn-qingdao.aliyuncs.com/20240501203628ptwondCGhItP67eb5ac72554b07800b22c542245e457.jpeg','MALE','11111111111','$2a$10$o/DHIt/eAMR175TgDV/PeeuEOpqW1N4Klft6obvs2zqBuiwMgLWOW'),('fe3d0d7d6eb34eb7a6b7985426cf8af7','2024-06-06 13:23:46.130879','2024-06-06 13:23:46.130817','默认用户',NULL,NULL,'13656987994','$2a$10$q7pey1P1/b3lO9nzFLKOb.ISrX7.lkktMjghwhgvNqvA.EjZZ2mg2');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-01-14 15:21:11
