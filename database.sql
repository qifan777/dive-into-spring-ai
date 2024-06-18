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
-- Table structure for table `ai_function`
--

DROP TABLE IF EXISTS `ai_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_function` (
  `id` varchar(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `editor_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
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
INSERT INTO `ai_function` VALUES ('4703133f93874c51bdc7860cc781632f','2024-06-08 15:27:29.806904','2024-06-08 15:39:58.594491','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','springSpELFunction','执行符合SpringSpE语法的表达式');
/*!40000 ALTER TABLE `ai_function` ENABLE KEYS */;
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
  `medias` json DEFAULT NULL,
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
INSERT INTO `ai_message` VALUES ('099c0d014bdf4a4da693d6c3e5eee9c0','2024-06-18 14:33:54.049532','2024-06-18 14:33:54.049532','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','我的名字是起凡','[]','5e7542f85bd247ffba192cbc7cd06c5e');
INSERT INTO `ai_message` VALUES ('0e0603e02f0e42ec83bd4afa41f319a9','2024-06-18 14:33:57.006347','2024-06-18 14:33:57.006347','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','很高兴很高兴认识你很高兴认识你，起凡！有什么很高兴认识你，起凡！有什么我能帮到你的吗？','[]','5e7542f85bd247ffba192cbc7cd06c5e');
INSERT INTO `ai_message` VALUES ('4c93364c696d45ea903817bddc3f8450','2024-06-18 14:27:07.877939','2024-06-18 14:27:07.877939','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','你好你好！有什么你好！有什么我能为你效劳的你好！有什么我能为你效劳的吗？','[]','5e7542f85bd247ffba192cbc7cd06c5e');
INSERT INTO `ai_message` VALUES ('9f0de4712575484fa050c2475453fa1f','2024-06-18 14:34:00.989601','2024-06-18 14:34:00.989601','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','我叫什么名字？','[]','5e7542f85bd247ffba192cbc7cd06c5e');
INSERT INTO `ai_message` VALUES ('d2cfbb52381c4cd090c0d030994a2d85','2024-06-18 14:27:05.442812','2024-06-18 14:27:05.442812','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','USER','你好','[]','5e7542f85bd247ffba192cbc7cd06c5e');
INSERT INTO `ai_message` VALUES ('ee44cb8d7a3544e189e6e6516a25c16a','2024-06-18 14:34:02.368642','2024-06-18 14:34:02.368642','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','ASSISTANT','你你叫你叫起你叫起凡。','[]','5e7542f85bd247ffba192cbc7cd06c5e');
/*!40000 ALTER TABLE `ai_message` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ai_role`
--

DROP TABLE IF EXISTS `ai_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ai_role` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) NOT NULL,
  `editor_id` varchar(32) NOT NULL,
  `name` varchar(255) NOT NULL COMMENT '角色名称',
  `description` varchar(32) NOT NULL,
  `icon` varchar(255) DEFAULT NULL COMMENT '图标',
  `prompts` json NOT NULL COMMENT '预置提示词',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_role`
--

LOCK TABLES `ai_role` WRITE;
/*!40000 ALTER TABLE `ai_role` DISABLE KEYS */;
INSERT INTO `ai_role` VALUES ('87dde7fd46b24b9c8836dd1d48348307','2024-06-01 21:07:10.219035','2024-06-01 21:07:10.219035','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','python助手','可以帮助你编写python代码','https://my-community.oss-cn-qingdao.aliyuncs.com/20240601210542起凡.jpg','[{\"type\": \"SYSTEM\", \"content\": [{\"text\": \"你是一个python助手可以帮助用户编写python程序\"}]}]');
/*!40000 ALTER TABLE `ai_role` ENABLE KEYS */;
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
INSERT INTO `ai_session` VALUES ('5e7542f85bd247ffba192cbc7cd06c5e','2024-06-18 14:01:21.242508','2024-06-18 14:01:21.241948','fe3d0d7d6eb34eb7a6b7985426cf8af7','fe3d0d7d6eb34eb7a6b7985426cf8af7','新的聊天');
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

-- Dump completed on 2024-06-18 15:12:28
