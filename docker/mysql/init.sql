CREATE DATABASE IF NOT EXISTS `chat-gpt` DEFAULT CHARACTER SET utf8mb4;
USE `chat-gpt`;


SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for chat_count
-- ----------------------------
DROP TABLE IF EXISTS `chat_count`;
CREATE TABLE `chat_count`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `tokens` int NULL DEFAULT NULL,
  `completion_tokens` int NULL DEFAULT NULL,
  `prompt_tokens` int NULL DEFAULT NULL,
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `create_time` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;


-- ----------------------------
-- Table structure for chat_prompt
-- ----------------------------
DROP TABLE IF EXISTS `chat_prompt`;
CREATE TABLE `chat_prompt`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `allow_context` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `prompt` varchar(190) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `create_time` datetime NOT NULL,
  `enabled` bit(1) NOT NULL DEFAULT b'1',
  `model_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of chat_prompt
-- ----------------------------
INSERT INTO `chat_prompt` VALUES (1, 'CLOSE', '算五行', '我有一个名字，帮我分析一下这个的五行。 这个名字是：#{content[\'name\']}', '2023-05-24 11:46:56', b'1', 'AINameAnalysis');
INSERT INTO `chat_prompt` VALUES (2, 'OPEN,CLOSE', '起名', '请根据我的要求帮我生成最多6个名称，并给出理由。要求如下：#{content[\'name\']}', '2023-05-31 11:49:10', b'1', 'AINameCreate');
INSERT INTO `chat_prompt` VALUES (3, 'OPEN', '自由对话', '#{content[\'name\']}', '2023-05-30 16:57:49', b'1', 'AINameAnalysis');

SET FOREIGN_KEY_CHECKS = 1;
