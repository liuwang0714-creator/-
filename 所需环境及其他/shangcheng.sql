CREATE DATABASE IF NOT EXISTS shangcheng DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE shangcheng;

/*
 Navicat Premium Dump SQL

 Source Server         : mysql
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : shangcheng

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 26/07/2026 10:01:37
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for address
-- ----------------------------
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '手机号',
  `addr` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '详细地址',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_addr_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_addr_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '收货地址表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of address
-- ----------------------------
INSERT INTO `address` VALUES (1, 3, '小刘', '12344445555', '四川省成都市');
INSERT INTO `address` VALUES (2, 3, '小张', '12566664444', '四川省成都市');

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `user_id` int(11) NOT NULL COMMENT '评论用户ID',
  `goods_id` int(11) NOT NULL COMMENT '评论商品ID',
  `file_id` int(11) NULL DEFAULT NULL COMMENT '评论图片ID',
  `like_count` int(11) NOT NULL DEFAULT 0 COMMENT '点赞总数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `to_coment_num` int(11) NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_goods_id`(`goods_id` ASC) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_file` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_goods` FOREIGN KEY (`goods_id`) REFERENCES `goods` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品一级评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (6, '测试评论', 3, 1, NULL, 0, '2026-06-26 23:25:02', 0);
INSERT INTO `comment` VALUES (7, '测试测试', 3, 1, 39, 0, '2026-07-04 20:46:52', 0);
INSERT INTO `comment` VALUES (8, '测试测试', 3, 1, 40, 0, '2026-07-04 20:47:26', 1);

-- ----------------------------
-- Table structure for comment_to
-- ----------------------------
DROP TABLE IF EXISTS `comment_to`;
CREATE TABLE `comment_to`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '二级评论ID',
  `comment` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `user_id` int(11) NOT NULL COMMENT '评论用户ID',
  `file_id` int(11) NULL DEFAULT NULL COMMENT '评论图片ID',
  `one_id` int(11) NOT NULL COMMENT '所属一级评论ID',
  `like_count` int(11) NOT NULL DEFAULT 0 COMMENT '点赞总数',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_one_id`(`one_id` ASC) USING BTREE,
  INDEX `idx_file_id`(`file_id` ASC) USING BTREE,
  CONSTRAINT `fk_ct_file` FOREIGN KEY (`file_id`) REFERENCES `file` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_ct_one_comment` FOREIGN KEY (`one_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_ct_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品二级评论表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of comment_to
-- ----------------------------
INSERT INTO `comment_to` VALUES (1, '测试回复', 3, 41, 8, 0, '2026-07-04 20:47:37');

-- ----------------------------
-- Table structure for file
-- ----------------------------
DROP TABLE IF EXISTS `file`;
CREATE TABLE `file`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件名',
  `file_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件访问地址',
  `file_size` bigint(20) NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `create_id` int(11) NULL DEFAULT NULL COMMENT '上传用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_id`(`create_id` ASC) USING BTREE,
  CONSTRAINT `fk_file_create_user` FOREIGN KEY (`create_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 42 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文件上传表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of file
-- ----------------------------
INSERT INTO `file` VALUES (1, NULL, 'c7f12aac-ebad-4d3a-acb5-a107ef03fe95.jpg', NULL, NULL, '2026-06-08 00:03:16');
INSERT INTO `file` VALUES (2, NULL, '7d05ccda-db7f-42eb-91e9-c792fc09ef17.jpg', NULL, 2, '2026-06-08 00:05:18');
INSERT INTO `file` VALUES (3, NULL, 'dd805661-3c90-4120-b1b6-1b57f01d0d26.jpg', NULL, 2, '2026-06-08 00:05:42');
INSERT INTO `file` VALUES (4, NULL, '31bd248a-a86b-4b7e-a6c5-ebc989c51654.jpg', NULL, 2, '2026-06-08 00:06:00');
INSERT INTO `file` VALUES (5, NULL, 'db5f142c-7206-498c-b004-2941b988c849.jpg', NULL, 2, '2026-06-08 00:06:10');
INSERT INTO `file` VALUES (6, NULL, '368f2cc8-f6d8-4e7c-bbaa-a354ff02e2ae.jpg', NULL, 2, '2026-06-08 00:06:13');
INSERT INTO `file` VALUES (7, NULL, '817717ab-81c5-4dfa-8cc3-48c4e876da6f.jpg', NULL, 2, '2026-06-08 00:06:15');
INSERT INTO `file` VALUES (8, NULL, 'a115a721-cab0-4344-bf35-fc987f6f6f66.jpg', NULL, 2, '2026-06-08 00:06:18');
INSERT INTO `file` VALUES (9, NULL, 'f28715b7-2c0a-47be-ba7e-57135219597a.jpg', NULL, 2, '2026-06-08 00:06:20');
INSERT INTO `file` VALUES (10, NULL, 'fa12b884-3f66-496e-a57a-03103013762c.jpg', NULL, 2, '2026-06-08 00:06:22');
INSERT INTO `file` VALUES (11, NULL, '1965b445-eda6-4836-9fa2-d3f0c5d66e6c.jpg', NULL, 2, '2026-06-08 00:06:25');
INSERT INTO `file` VALUES (12, NULL, '610e3dfe-b93d-490d-8906-c6237fe719a4.jpg', NULL, 2, '2026-06-08 00:06:27');
INSERT INTO `file` VALUES (13, NULL, '7b40539c-2160-4721-9ab4-7e700c7c0238.jpg', NULL, 3, '2026-06-08 00:08:17');
INSERT INTO `file` VALUES (14, NULL, '68591735-b61a-4564-a903-b3a90d331a6c.jpg', NULL, 3, '2026-06-08 00:08:46');
INSERT INTO `file` VALUES (15, NULL, 'c339935f-9e09-4708-97e2-c2e8781b318e.jpg', NULL, 3, '2026-06-08 00:09:14');
INSERT INTO `file` VALUES (16, NULL, 'df67288f-3c05-49d2-b3d3-f80a1d2e3a36.png', NULL, 2, '2026-06-08 19:22:28');
INSERT INTO `file` VALUES (17, NULL, 'df02abc5-7969-48fc-90d9-9e66670c3102.png', NULL, 3, '2026-06-09 22:10:39');
INSERT INTO `file` VALUES (18, NULL, '7670c5c3-46bd-48ea-a17e-bd14d1e12f42.png', NULL, 3, '2026-06-09 22:10:58');
INSERT INTO `file` VALUES (19, NULL, '7cf4661a-1f79-4b02-8085-59855ffa3374.jpg', NULL, 2, '2026-06-16 09:13:51');
INSERT INTO `file` VALUES (20, NULL, 'f3e995dc-8eab-4428-b9bf-abf5e8537e40.jpg', NULL, 2, '2026-06-16 09:14:58');
INSERT INTO `file` VALUES (21, NULL, '3bb32563-4fc9-409d-aa02-2c0b0f86051f.jpg', NULL, 2, '2026-06-16 09:16:07');
INSERT INTO `file` VALUES (22, NULL, '555e814a-90e5-47f0-8cd2-2f433f976490.jpg', NULL, 2, '2026-06-16 09:17:16');
INSERT INTO `file` VALUES (23, NULL, '8ca86dc1-7fdb-4777-a100-906afd257147.jpg', NULL, 2, '2026-06-16 09:18:55');
INSERT INTO `file` VALUES (24, NULL, '020af23a-2494-4a6f-b635-b8d7d40f0ca6.jpg', NULL, 2, '2026-06-16 09:19:55');
INSERT INTO `file` VALUES (25, NULL, '75913ef4-f185-4afe-abef-9defdf787758.jpg', NULL, 2, '2026-06-16 09:22:22');
INSERT INTO `file` VALUES (26, NULL, '15ad1ab6-ead1-4af7-910f-fd00d289f6f8.jpg', NULL, 2, '2026-06-16 09:26:46');
INSERT INTO `file` VALUES (27, NULL, '1603378b-1261-4779-98b5-823ce0e2fe0c.jpg', NULL, 2, '2026-06-16 09:28:17');
INSERT INTO `file` VALUES (28, NULL, '00315499-5329-4b09-a24b-4835f4ac97a8.jpg', NULL, 2, '2026-06-16 09:28:53');
INSERT INTO `file` VALUES (29, NULL, 'aef98b68-5dde-4c08-9978-50dc202511ac.jpg', NULL, 2, '2026-06-16 09:30:43');
INSERT INTO `file` VALUES (30, NULL, '22305447-838f-4679-be92-57d61aefb9c7.jpg', NULL, 2, '2026-06-16 09:31:37');
INSERT INTO `file` VALUES (31, NULL, '48b056d8-0122-4868-815a-305147927044.jpg', NULL, 2, '2026-06-16 09:32:37');
INSERT INTO `file` VALUES (32, NULL, 'f95c2af2-e77c-4d11-9879-4a6d57a89b0e.jpg', NULL, 2, '2026-06-16 09:52:22');
INSERT INTO `file` VALUES (38, NULL, '57947bc7-ef79-4170-873d-bd1ffaf3b195.png', NULL, 2, '2026-06-29 19:35:50');
INSERT INTO `file` VALUES (39, NULL, 'c41af607-9f3d-4f76-97e9-eaaf87c0d87a.png', NULL, 3, '2026-07-04 20:46:49');
INSERT INTO `file` VALUES (40, NULL, '25b05bd2-57b5-43ba-9818-c7f95c69513d.png', NULL, 3, '2026-07-04 20:47:24');
INSERT INTO `file` VALUES (41, NULL, '57544ebb-a17a-410c-b9b5-db2579e0764f.png', NULL, 3, '2026-07-04 20:47:36');

-- ----------------------------
-- Table structure for goods
-- ----------------------------
DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `goods_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `price` double NOT NULL COMMENT '价格',
  `stock` int(11) NOT NULL DEFAULT 0 COMMENT '库存',
  `description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品描述',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_id` int(11) NULL DEFAULT NULL COMMENT '发布商家用户ID',
  `goods_file_id` int(11) NULL DEFAULT NULL COMMENT '商品主图文件ID',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_create_id`(`create_id` ASC) USING BTREE,
  INDEX `idx_goods_file_id`(`goods_file_id` ASC) USING BTREE,
  CONSTRAINT `fk_goods_create_user` FOREIGN KEY (`create_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_goods_file` FOREIGN KEY (`goods_file_id`) REFERENCES `file` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of goods
-- ----------------------------
INSERT INTO `goods` VALUES (1, '第一条商品低价值', 0.99, 90, '第一条商品低价值第一条商品低价值第一条商品低价值', '2026-06-08 00:05:20', '2026-06-27 10:47:47', 2, 2);
INSERT INTO `goods` VALUES (2, '第一条商品0元商品', 0, 0, '第一条商品0元商品第一条商品0元商品第一条商品0元商品', '2026-06-08 00:05:43', '2026-06-08 00:05:43', 2, 3);
INSERT INTO `goods` VALUES (3, '第一条商品高价值', 9999, 97, '第一条商品高价值第一条商品高价值第一条商品高价值', '2026-06-08 00:06:01', '2026-06-15 14:09:05', 2, 4);
INSERT INTO `goods` VALUES (4, '测试商品1', 45, 5, '测试', '2026-06-08 15:48:00', '2026-06-15 14:09:27', 2, 2);
INSERT INTO `goods` VALUES (5, '测试商品2', 87, 3, '测试', '2026-06-08 15:48:01', '2026-06-08 16:53:34', 2, 2);
INSERT INTO `goods` VALUES (6, '测试商品3', 56, 0, '测试', '2026-06-08 15:48:02', '2026-06-08 17:00:46', 2, 2);
INSERT INTO `goods` VALUES (7, '测试商品4', 34, 4, '测试', '2026-06-08 15:48:03', '2026-06-08 17:01:18', 2, 2);
INSERT INTO `goods` VALUES (8, '测试商品5', 56, 3, '测试', '2026-06-08 15:48:04', '2026-06-08 16:17:10', 2, 2);
INSERT INTO `goods` VALUES (9, '测试商品6', 87, 6, '测试', '2026-06-08 15:48:05', '2026-06-08 16:18:48', 2, 2);
INSERT INTO `goods` VALUES (10, '测试商品7', 43, 5, '测试', '2026-06-08 15:48:05', '2026-06-08 16:17:13', 2, 2);
INSERT INTO `goods` VALUES (11, '测试商品8', 75, 7, '测试', '2026-06-08 15:48:06', '2026-06-08 16:17:14', 2, 2);
INSERT INTO `goods` VALUES (12, '测试商品9', 27, 7, '测试', '2026-06-08 15:48:07', '2026-06-08 16:17:15', 2, 2);
INSERT INTO `goods` VALUES (13, '测试商品0', 95, 7, '测试', '2026-06-08 15:48:08', '2026-06-08 16:17:17', 2, 2);
INSERT INTO `goods` VALUES (14, '测试商品11', 16, 7, '测试', '2026-06-08 15:48:09', '2026-06-08 16:17:21', 2, 2);
INSERT INTO `goods` VALUES (15, 'ces', 222, 0, '测试测试测试测试测试测试测试测试测试测试', '2026-06-08 19:22:30', '2026-06-08 19:22:30', 2, 16);

-- ----------------------------
-- Table structure for im_message
-- ----------------------------
DROP TABLE IF EXISTS `im_message`;
CREATE TABLE `im_message`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '消息ID',
  `from_user_id` bigint(20) NOT NULL COMMENT '发送者ID',
  `to_user_id` bigint(20) NULL DEFAULT NULL COMMENT '接收者ID，NULL表示群聊',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '消息内容',
  `msg_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'TEXT' COMMENT '消息类型: TEXT/IMAGE',
  `chat_type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'PUBLIC' COMMENT '聊天类型: PUBLIC/PRIVATE',
  `send_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_from_user`(`from_user_id` ASC) USING BTREE,
  INDEX `idx_to_user`(`to_user_id` ASC) USING BTREE,
  INDEX `idx_send_time`(`send_time` ASC) USING BTREE,
  INDEX `idx_chat_type`(`chat_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = 'IM消息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of im_message
-- ----------------------------
INSERT INTO `im_message` VALUES (27, 3, 2, '你好', 'TEXT', 'PUBLIC', '2026-06-16 08:54:43');
INSERT INTO `im_message` VALUES (28, 2, 3, '在的', 'TEXT', 'PUBLIC', '2026-06-16 08:55:54');
INSERT INTO `im_message` VALUES (29, 2, 1, '您好', 'TEXT', 'PUBLIC', '2026-06-16 09:37:16');

-- ----------------------------
-- Table structure for like_comment
-- ----------------------------
DROP TABLE IF EXISTS `like_comment`;
CREATE TABLE `like_comment`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL COMMENT '点赞用户ID',
  `comment_id` int(11) NOT NULL COMMENT '评论ID（支持一级/二级，逻辑关联）',
  `create_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_comment_user`(`comment_id` ASC, `user_id` ASC) USING BTREE COMMENT '防重复点赞',
  INDEX `idx_comment_id`(`comment_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论点赞表（合并一级/二级）' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of like_comment
-- ----------------------------

-- ----------------------------
-- Table structure for order
-- ----------------------------
DROP TABLE IF EXISTS `order`;
CREATE TABLE `order`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '订单主键',
  `order_id` varchar(256) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单号',
  `good_id` int(11) NULL DEFAULT NULL COMMENT '商品ID，关联goods表id',
  `buyNum` int(11) NULL DEFAULT NULL COMMENT '购买数量',
  `addr_id` int(11) NULL DEFAULT NULL COMMENT '地址ID，关联address表id',
  `coupon` int(11) NULL DEFAULT NULL COMMENT '优惠券ID，关联coupon表id',
  `fahuo` int(11) NULL DEFAULT NULL COMMENT '发货状态 0未发货 1已发货',
  `user_id` int(11) NULL DEFAULT NULL COMMENT '下单用户ID，关联user表id',
  `merchant_id` int(11) NULL DEFAULT NULL COMMENT '商家ID，关联user表id',
  `beizhu` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '订单备注',
  `total_price` decimal(10, 2) NULL DEFAULT NULL COMMENT '订单总价',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
  `state` int(11) NULL DEFAULT NULL COMMENT '订单状态 0待支付 1已支付 2已取消',
  `pay_time` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付时间',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `addr_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '详细地址',
  `express_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '快递单号',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of order
-- ----------------------------
INSERT INTO `order` VALUES (1, '457063830955102208', 1, 1, NULL, NULL, 2, 3, 2, '', 0.99, '2026-06-15 14:08:29', '2026-06-15 14:08:29', 3, NULL, NULL, NULL, NULL, '');
INSERT INTO `order` VALUES (2, '457063982864404480', 3, 1, 1, NULL, 1, 3, 2, '', 9999.00, '2026-06-15 14:09:05', '2026-06-15 14:09:05', 2, NULL, '小刘', '12344445555', '四川省成都市', '111');
INSERT INTO `order` VALUES (3, '457064073171963904', 4, 1, 1, NULL, 1, 3, 2, '测试', 45.00, '2026-06-15 14:09:26', '2026-06-15 14:09:26', 2, NULL, '小刘', '12344445555', '四川省成都市', '111');
INSERT INTO `order` VALUES (4, '461361980255637504', 1, 1, 1, 0, 1, 3, 2, '', 0.99, '2026-06-27 10:47:47', '2026-06-27 10:47:47', 1, NULL, '小刘', '12344445555', '四川省成都市', '');

-- ----------------------------
-- Table structure for pay
-- ----------------------------
DROP TABLE IF EXISTS `pay`;
CREATE TABLE `pay`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '支付记录主键',
  `order_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单ID（关联order表order_id）',
  `pay_type` tinyint(4) NULL DEFAULT 0 COMMENT '支付方式 1=微信支付 2=支付宝支付',
  `pay_status` tinyint(4) NULL DEFAULT 0 COMMENT '支付状态 0=待支付 1=已支付 2=支付失败 3=已退款',
  `total_price` decimal(10, 2) NULL DEFAULT 0.00 COMMENT '订单支付金额',
  `transaction_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT '' COMMENT '第三方支付订单号（微信/支付宝订单号）',
  `pay_time` datetime NULL DEFAULT NULL COMMENT '支付成功时间',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_pay_status`(`pay_status` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '支付记录表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of pay
-- ----------------------------
INSERT INTO `pay` VALUES (1, '457063830955102208', 1, 1, 0.99, '', NULL, '2026-06-15 14:08:32', '2026-06-15 14:08:32');
INSERT INTO `pay` VALUES (2, '457063982864404480', 1, 1, 9999.00, '', NULL, '2026-06-15 14:09:07', '2026-06-15 14:09:07');
INSERT INTO `pay` VALUES (3, '457064073171963904', 1, 1, 45.00, '', NULL, '2026-06-15 14:09:31', '2026-06-15 14:09:31');
INSERT INTO `pay` VALUES (4, '461361980255637504', 1, 1, 0.99, '', NULL, '2026-06-27 10:47:50', '2026-06-27 10:47:50');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `role_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色编码',
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '角色名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`role_code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '角色表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES (1, 'admin', '管理员');
INSERT INTO `sys_role` VALUES (2, 'merchant', '商家');
INSERT INTO `sys_role` VALUES (3, 'user', '普通用户');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` int(11) NOT NULL,
  `role_id` int(11) NOT NULL,
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
  UNIQUE INDEX `uk_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  CONSTRAINT `fk_sur_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_sur_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户角色多对多关联表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES (1, 1);
INSERT INTO `sys_user_role` VALUES (2, 2);
INSERT INTO `sys_user_role` VALUES (3, 3);
INSERT INTO `sys_user_role` VALUES (4, 3);
INSERT INTO `sys_user_role` VALUES (5, 3);
INSERT INTO `sys_user_role` VALUES (6, 3);
INSERT INTO `sys_user_role` VALUES (7, 3);
INSERT INTO `sys_user_role` VALUES (8, 3);
INSERT INTO `sys_user_role` VALUES (9, 3);

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '密码',
  `login_type` tinyint(4) NOT NULL DEFAULT 1 COMMENT '1账号密码 2微信小程序',
  `openid` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '微信openid，微信用户独有',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '手机号',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `user_file_id` int(11) NULL DEFAULT NULL COMMENT '用户头像文件ID',
  `birthday` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '生日',
  `state` tinyint(4) NOT NULL DEFAULT 0 COMMENT '用户状态：0正常 1封禁 2注销',
  `ban_time` bigint(20) NOT NULL DEFAULT 0 COMMENT '封禁截止时间戳（毫秒）',
  `role_id` int(11) NULL DEFAULT NULL COMMENT '用户角色ID（关联sys_role表，冗余字段简化查询）',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE,
  UNIQUE INDEX `uk_openid`(`openid` ASC) USING BTREE COMMENT '微信openid唯一约束',
  INDEX `idx_role_id`(`role_id` ASC) USING BTREE,
  INDEX `idx_user_file_id`(`user_file_id` ASC) USING BTREE,
  CONSTRAINT `fk_user_file` FOREIGN KEY (`user_file_id`) REFERENCES `file` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT,
  CONSTRAINT `fk_user_role` FOREIGN KEY (`role_id`) REFERENCES `sys_role` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '用户表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '21232f297a57a5a743894a0e4a801fc3', 1, NULL, 'admin', NULL, '2026-06-29 19:07:28', '2026-06-29 19:09:19', 1, NULL, 0, 0, 1);
INSERT INTO `user` VALUES (2, 'shangjia', '7a122ca4b332f2b3923a585b6dceb178', 1, NULL, 'shangjia', NULL, '2026-06-09 23:05:03', '2026-06-26 23:21:11', 4, NULL, 0, 0, 2);
INSERT INTO `user` VALUES (3, 'yonghu', 'b05004cbc0badc65d3db340fae8dc74f', 1, NULL, 'yonghu', '13383403941', '2026-06-09 23:05:14', '2026-06-15 23:41:01', 6, '2021-02-01', 0, 0, 3);
INSERT INTO `user` VALUES (4, 'yonghu1', 'e2fc30484e5204b6970487a2f1b2c632', 1, NULL, 'yonghu1', NULL, '2026-06-09 23:05:19', '2026-06-09 23:10:15', 7, NULL, 1, 0, 3);
INSERT INTO `user` VALUES (5, 'yonghu2', '475076a10bd815b8fa0a95876ac02be8', 1, NULL, 'yonghu2', NULL, '2026-06-09 23:08:53', '2026-06-09 23:10:20', 8, NULL, 2, 0, 3);
INSERT INTO `user` VALUES (6, 'yonghu3', 'f8e886993625bee7f65dd4081a9cfa9e', 1, NULL, 'yonghu3', NULL, '2026-06-09 23:09:05', '2026-06-09 23:10:23', 9, NULL, 3, 0, 3);
INSERT INTO `user` VALUES (7, 'yonghu4', '25982fdb9ec9bbc40a07c97988f9a35b', 1, NULL, 'yonghu4', NULL, '2026-06-09 23:09:13', '2026-06-15 14:10:23', 10, NULL, 0, 1817500223104, 3);
INSERT INTO `user` VALUES (8, 'yonghu5', 'e118fab4c73b6bb1a5a1dc7c41a54747', 1, NULL, 'yonghu5', NULL, '2026-06-09 23:09:23', '2026-06-09 23:09:48', 11, NULL, 0, 0, 3);
INSERT INTO `user` VALUES (9, 'yonghu6', '907fbd363b6a5a2e51041be8e66490f1', 1, NULL, 'yonghu6', NULL, '2026-06-09 23:09:32', '2026-06-09 23:09:51', 12, NULL, 0, 0, 3);

-- ----------------------------
-- Triggers structure for table sys_user_role
-- ----------------------------
DROP TRIGGER IF EXISTS `sync_sur_to_user_insert`;
delimiter ;;
CREATE TRIGGER `sync_sur_to_user_insert` AFTER INSERT ON `sys_user_role` FOR EACH ROW BEGIN
    UPDATE USER SET role_id = NEW.role_id WHERE id = NEW.user_id;
END
;;
delimiter ;

-- ----------------------------
-- Triggers structure for table sys_user_role
-- ----------------------------
DROP TRIGGER IF EXISTS `sync_sur_to_user_update`;
delimiter ;;
CREATE TRIGGER `sync_sur_to_user_update` AFTER UPDATE ON `sys_user_role` FOR EACH ROW BEGIN
    UPDATE USER SET role_id = NEW.role_id WHERE id = NEW.user_id;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;
