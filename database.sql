-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: knowledge_base
-- ------------------------------------------------------
-- Server version	8.0.36

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
INSERT INTO `ai_message` VALUES ('05c523bf586b422e8f19d32a6bab17b6','2024-06-23 22:08:33.529844','2024-06-23 22:08:33.529844','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','今天的日期是2024年06月23日。',NULL,'b495186b9259494792a9fe4ce8807f25');
INSERT INTO `ai_message` VALUES ('1d14d513ae8c4ffda0fb50df6e24ba77','2024-06-20 22:08:28.389711','2024-06-20 22:08:28.389711','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','C:\\Users\\Administrator\\Desktop\\2023年工作总结及2024年工作展望-林家成.docx，这份文档的内容是什么？','[]','c4d192c4b2de48bcae585f5b9672cd1d');
INSERT INTO `ai_message` VALUES ('34d65ad69e4b470794da11732fe44a7d','2024-06-23 22:08:33.337776','2024-06-23 22:08:33.337776','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','今天的日期是多少？','[]','b495186b9259494792a9fe4ce8807f25');
INSERT INTO `ai_message` VALUES ('ba161500bec84b388a6501b3be333b45','2024-06-20 22:08:28.649439','2024-06-20 22:08:28.649439','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','2023年的工作总结及2024年工作展望文档内容概要如下：\n\n1、**工作回顾重点：**\n   - 成功运用Python脚本在一经上云项目中实现Oracle脚本批量迁移、实体批量登记等，极大提升了工作效率（速度提升70倍以上），并提前完成年度工作任务。\n   - 领导新智慧工厂项目的前端与后端框架搭建，采用更高效、安全的技术栈，促进团队快速投入业务开发。重构并优化了基础功能模块，如菜单框架、租户管理等，封装为易用组件。\n   - 开发了脚本依赖分析工具，通过SQL语法树解析等技术，有效辅助故障检测、代码规范检查及开发效率，与部门其他系统（如指标库、报表系统）集成，增强功能实用性。\n\n2、**个人成长与进步：**\n   - 今年最大的成就是深入学习了数据开发知识，综合软件开发能力使自己更能满足用户需求，相比同行具有更全面的技能组合。\n\n3、**应对挑战的方法：**\n   - 面对SQL语法解析等高难度任务，通过自学源码、复习理论基础，成功开发出工具，体现了自我驱动力和解决问题的能力。\n\n4、**反思与改进空间：**\n   - 希望能在项目中实践深度学习、机器学习技术，这是未来努力的一个方向。\n\n5、**提质增效的建议：**\n   - 强调代码审查和个人技能提升，鼓励使用先进技术和工具，以及创建持续学习的环境，以保持竞争力。\n\n6、**对当前开发工作的建议：**\n   - 提倡技术分享会议，打破知识孤岛，促进新老员工间的技术交流和学习，平衡稳定与创新的需求。\n\n7、**其他建议：**\n   - 加强跨部门合作，以新智慧工厂项目为范例，推动更多跨领域协作成果。\n\n对于2024年的展望虽未详细展开，但基于上述总结，可能包括深化技术应用、促进团队协作、继续个人技能升级等方面。',NULL,'c4d192c4b2de48bcae585f5b9672cd1d');
/*!40000 ALTER TABLE `ai_message` ENABLE KEYS */;
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
INSERT INTO `ai_session` VALUES ('b495186b9259494792a9fe4ce8807f25','2024-06-18 22:01:51.459293','2024-06-18 22:01:51.459293','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','新的聊天');
INSERT INTO `ai_session` VALUES ('c4d192c4b2de48bcae585f5b9672cd1d','2024-06-18 22:01:45.819825','2024-06-18 22:01:45.819317','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','新的聊天');
INSERT INTO `ai_session` VALUES ('dcd32fd9f7fc4d0fb9a5b7dc9ff0dbc8','2024-06-18 21:57:09.390651','2024-06-18 21:57:09.390651','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','新的聊天');
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
INSERT INTO `user` VALUES ('dcd256e2412f4162a6a5fcbd5cfedc84','2024-05-01 16:52:43.364225','2024-05-19 21:30:34.686818','起凡','https://my-community.oss-cn-qingdao.aliyuncs.com/20240501203628ptwondCGhItP67eb5ac72554b07800b22c542245e457.jpeg','MALE','11111111111','$2a$10$o/DHIt/eAMR175TgDV/PeeuEOpqW1N4Klft6obvs2zqBuiwMgLWOW');
INSERT INTO `user` VALUES ('fe3d0d7d6eb34eb7a6b7985426cf8af7','2024-06-06 13:23:46.130879','2024-06-06 13:23:46.130817','默认用户',NULL,NULL,'13656987994','$2a$10$q7pey1P1/b3lO9nzFLKOb.ISrX7.lkktMjghwhgvNqvA.EjZZ2mg2');
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

-- Dump completed on 2024-06-24 21:11:15
