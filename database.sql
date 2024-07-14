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
INSERT INTO `ai_message` VALUES ('1b23bdbb845343979ea7daaba5946507','2024-07-13 23:51:15.150897','2024-07-13 23:51:15.150897','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','USER','喋血街头的导游是谁？','[]','87f5e3660dfb4cf7955f5ec08bddb2f0');
INSERT INTO `ai_message` VALUES ('66539b719d0a4311be41ea8cf8ccf15c','2024-07-13 23:51:55.205343','2024-07-13 23:51:55.205343','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','ASSISTANT','是一动漫格的图片展示了一位着护目镜的角色。他穿着橙色的围巾，背景是一个色的调色板。这种画风和角色的外观让人想到日本动漫中的角色，但具体是哪一部作品或是哪一个角色，无法从图片中确定。',NULL,'87f5e3660dfb4cf7955f5ec08bddb2f0');
INSERT INTO `ai_message` VALUES ('eb2b0ba539944555a067ef4f023614d3','2024-07-13 23:51:15.605627','2024-07-13 23:51:15.605627','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','ASSISTANT','根据提供的表格数据，《喋血街头》的导演是吴宇森 (John Woo)。',NULL,'87f5e3660dfb4cf7955f5ec08bddb2f0');
INSERT INTO `ai_message` VALUES ('f0658d3238c646fcb82f9893bb5ec9f1','2024-07-13 23:51:54.851946','2024-07-13 23:51:54.851946','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','USER','这张图片的内容是什么？','[{\"data\": \"https://my-community.oss-cn-qingdao.aliyuncs.com/20240713235121微信图片_20240330115005.jpg\", \"type\": \"image\"}]','87f5e3660dfb4cf7955f5ec08bddb2f0');
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
  `params` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ai_session`
--

LOCK TABLES `ai_session` WRITE;
/*!40000 ALTER TABLE `ai_session` DISABLE KEYS */;
INSERT INTO `ai_session` VALUES ('87f5e3660dfb4cf7955f5ec08bddb2f0','2024-07-13 23:27:46.203755','2024-07-13 23:51:47.403512','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','新的聊天','{\"file\": \"https://my-community.oss-cn-qingdao.aliyuncs.com/20240713235048movie.csv\", \"enableAgent\": false, \"enableProfession\": false, \"enableVectorStore\": false}');
/*!40000 ALTER TABLE `ai_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `feedback`
--

DROP TABLE IF EXISTS `feedback`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `feedback` (
  `id` varchar(36) NOT NULL,
  `created_time` datetime(6) NOT NULL,
  `edited_time` datetime(6) NOT NULL,
  `creator_id` varchar(32) NOT NULL,
  `editor_id` varchar(32) NOT NULL,
  `content` text NOT NULL,
  `pictures` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `feedback`
--

LOCK TABLES `feedback` WRITE;
/*!40000 ALTER TABLE `feedback` DISABLE KEYS */;
INSERT INTO `feedback` VALUES ('e8f108ce73ec4017b456e69cb9447ebe','2024-07-14 09:39:37.975206','2024-07-14 09:39:37.975206','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','你好','[\"https://my-community.oss-cn-qingdao.aliyuncs.com/20240714093843etl-pipeline.jpg\", \"https://my-community.oss-cn-qingdao.aliyuncs.com/20240714093850微信图片_20240330115005.jpg\"]');
INSERT INTO `feedback` VALUES ('ea709c4ee8b04a6b871491f3c39b8c76','2024-07-14 09:46:39.526030','2024-07-14 09:46:39.526030','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','反馈测试','[\"https://my-community.oss-cn-qingdao.aliyuncs.com/20240714094626etl-pipeline.jpg\", \"https://my-community.oss-cn-qingdao.aliyuncs.com/20240714094631微信图片_20240330115005.jpg\"]');
INSERT INTO `feedback` VALUES ('f298675cb4874cdcb76d1f77afb612b2','2024-07-14 09:44:17.459765','2024-07-14 09:44:17.459765','dcd256e2412f4162a6a5fcbd5cfedc84','dcd256e2412f4162a6a5fcbd5cfedc84','你好','[\"https://my-community.oss-cn-qingdao.aliyuncs.com/20240714094412screenshot-1717656203937.png\", \"https://my-community.oss-cn-qingdao.aliyuncs.com/20240714094416微信图片_20240330115005.jpg\"]');
/*!40000 ALTER TABLE `feedback` ENABLE KEYS */;
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
INSERT INTO `user` VALUES ('24736e4789f24bf289fbad291c3f835d','2024-06-24 21:51:02.564141','2024-06-24 21:51:02.564141',NULL,NULL,NULL,'13656987995','$2a$10$LhsgAUfNRPD/NQtv/wIN4O5ch9Y8hhkU7X8SvtXQPeiLtlRRCztVa');
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

-- Dump completed on 2024-07-14 17:46:43
