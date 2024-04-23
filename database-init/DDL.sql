CREATE DATABASE `comprehensive-design`;
USE `comprehensive-design`;

-- 用户表
CREATE TABLE `t_user`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `password`      varchar(512) DEFAULT NULL COMMENT '密码',
    `real_name`     varchar(256) DEFAULT NULL COMMENT '真实姓名',
    `phone`         varchar(128) DEFAULT NULL COMMENT '手机号',
    `mail`          varchar(512) DEFAULT NULL COMMENT '邮箱',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username` (`username`) USING BTREE
) ENGINE = InnoDB
  AUTO_INCREMENT = 1726253659068588035
  DEFAULT CHARSET = utf8mb4;

DROP TABLE IF EXISTS `t_history`;
-- 历史记录表
CREATE TABLE `t_history`
(
    `id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `username`      varchar(256) DEFAULT NULL COMMENT '用户名',
    `topic`         varchar(256) DEFAULT NULL COMMENT '展示主题',
    `img_name`      varchar(512) DEFAULT NULL COMMENT '图片文件名',
    `deletion_time` bigint(20)   DEFAULT NULL COMMENT '注销时间戳',
    `create_time`   datetime     DEFAULT NULL COMMENT '创建时间',
    `update_time`   datetime     DEFAULT NULL COMMENT '修改时间',
    `del_flag`      tinyint(1)   DEFAULT NULL COMMENT '删除标识 0：未删除 1：已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `idx_unique_username_imgName` (`username`, `img_name`) USING BTREE

) ENGINE = InnoDB
  AUTO_INCREMENT = 1726253659068588035
  DEFAULT CHARSET = utf8mb4;


