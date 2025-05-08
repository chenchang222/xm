/*
 Navicat Premium Data Transfer

 Source Server         : cc
 Source Server Type    : MySQL
 Source Server Version : 80032
 Source Host           : localhost:3306
 Source Schema         : xm

 Target Server Type    : MySQL
 Target Server Version : 80032
 File Encoding         : 65001

 Date: 12/04/2025 10:10:55
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for activity
-- ----------------------------
DROP TABLE IF EXISTS `activity`;
CREATE TABLE `activity`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动类型',
  `location` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动地点',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '标题',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '描述',
  `start_Time` date NULL DEFAULT NULL COMMENT '开始时间',
  `end_Time` date NULL DEFAULT NULL COMMENT '结束时间',
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '状态',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '活动信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for activity_type
-- ----------------------------
DROP TABLE IF EXISTS `activity_type`;
CREATE TABLE `activity_type`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动类型',
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '具体描述',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of activity_type
-- ----------------------------
INSERT INTO `activity_type` VALUES (1, '社会福利类', '如为老人服务，为残疾人服务，为妇女儿童提供陪护、慰藉、辅助等服务');
INSERT INTO `activity_type` VALUES (2, '文化娱乐类', '');
INSERT INTO `activity_type` VALUES (3, '医疗卫生类', NULL);
INSERT INTO `activity_type` VALUES (4, '环保类', NULL);
INSERT INTO `activity_type` VALUES (5, '权益类', NULL);
INSERT INTO `activity_type` VALUES (6, '治安类', NULL);
INSERT INTO `activity_type` VALUES (7, '救援类', NULL);

-- ----------------------------
-- Table structure for employee
-- ----------------------------
DROP TABLE IF EXISTS `employee`;
CREATE TABLE `employee`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `no` int(0) NULL DEFAULT NULL COMMENT '工号',
  `age` int(0) NULL DEFAULT NULL COMMENT '年龄',
  `description` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '介绍',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 29 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '管理员信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of employee
-- ----------------------------
INSERT INTO `employee` VALUES (1, 'aaa', '123', 'EMP', '文绮', '女', 101, 3, '一名志愿者管理员');
INSERT INTO `employee` VALUES (2, 'ccc', '123', 'EMP', '鱼鱼', '男', 102, 21, NULL);
INSERT INTO `employee` VALUES (3, 'sss', '123', 'EMP', '刘姐', '女', 103, 20, NULL);
INSERT INTO `employee` VALUES (4, 'bbb', '123', 'EMP', '黎哥', '男', 104, 20250402, NULL);
INSERT INTO `employee` VALUES (5, 'ddd', '123', 'EMP', '任5', '女', 56, 12, '牛');
INSERT INTO `employee` VALUES (13, 'fff', '123', 'EMP', '小猫', '女', 113, 3, '一只小猫');
INSERT INTO `employee` VALUES (14, 'eee', '123', 'EMP', '老何', '女', 152, 12, NULL);
INSERT INTO `employee` VALUES (15, 'ggg', '123', 'EMP', '来', '女', 121, 21, NULL);
INSERT INTO `employee` VALUES (16, 'hhh', '123', 'EMP', '来', '女', 121, 21, NULL);
INSERT INTO `employee` VALUES (17, 'iii', '123', 'EMP', '和', '女', 112, 33, NULL);
INSERT INTO `employee` VALUES (18, 'jjj', '123', 'EMP', '他', '女', 2, 2, '哈哈');
INSERT INTO `employee` VALUES (19, 'kkk', '123', 'EMP', '啊', '男', 145, 32, '胡');
INSERT INTO `employee` VALUES (20, 'lll', '123', 'EMP', '你', '男', 145, 32, '是');
INSERT INTO `employee` VALUES (21, 'mmm', '123', 'EMP', '主', '男', 144, 22, '2');
INSERT INTO `employee` VALUES (22, 'nnn', '123', 'EMP', '主', '男', 144, 22, '2');
INSERT INTO `employee` VALUES (23, 'uuu', '123', 'EMP', NULL, NULL, NULL, NULL, NULL);
INSERT INTO `employee` VALUES (24, 'vvv', '123', 'EMP', '你好', '女', 222, 22, '下');
INSERT INTO `employee` VALUES (25, 'www', '123', 'EMP', '文绮', '女', 101, 4, '一名志愿者管理员');
INSERT INTO `employee` VALUES (26, 'xxx', '123', 'EMP', '任5', '女', 253, 25, '扁桃仁');
INSERT INTO `employee` VALUES (27, 'yyy', '123', 'EMP', '文绮', '女', 101, 4, '一名志愿者管理员');
INSERT INTO `employee` VALUES (28, 'zzz', '123', 'EMP', '文绮', '女', 101, 4, '一名志愿者管理员');

-- ----------------------------
-- Table structure for infomation
-- ----------------------------
DROP TABLE IF EXISTS `infomation`;
CREATE TABLE `infomation`  (
  `id` int(0) NOT NULL AUTO_INCREMENT,
  `activity_id` int(0) NOT NULL COMMENT '活动id',
  `activity_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '活动名称',
  `num` int(0) NULL DEFAULT NULL COMMENT '报名人数',
  `volunteer_id` int(0) NULL DEFAULT NULL COMMENT '志愿者id',
  `volunteer_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '志愿者姓名',
  `qq` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '志愿者联系方式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '报名信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for volunteer
-- ----------------------------
DROP TABLE IF EXISTS `volunteer`;
CREATE TABLE `volunteer`  (
  `id` int(0) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '账号',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '密码',
  `role` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '姓名',
  `age` int(0) NULL DEFAULT NULL COMMENT '年龄',
  `sex` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '性别',
  `no` int(0) NULL DEFAULT NULL COMMENT '志愿者编号',
  `contact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系方式',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of volunteer
-- ----------------------------
INSERT INTO `volunteer` VALUES (1, '何1', '123', 'VOL', '何的', 33, '男', 125, '548151');
INSERT INTO `volunteer` VALUES (2, '何2', '123', 'VOL', '热狗', 12, '女', NULL, '231556');
INSERT INTO `volunteer` VALUES (3, '123', '123', 'VOL', '123', 77, '男', 123, '123');

SET FOREIGN_KEY_CHECKS = 1;
