/*
Navicat MySQL Data Transfer

Source Server         : 105
Source Server Version : 50530
Source Host           : 192.168.200.105:3306
Source Database       : sms

Target Server Type    : MYSQL
Target Server Version : 50530
File Encoding         : 65001

Date: 2013-07-12 09:22:32
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `SMS_FAILURE`
-- ----------------------------
DROP TABLE IF EXISTS `SMS_FAILURE`;
CREATE TABLE `SMS_FAILURE` (
`ID`  int(11) NOT NULL AUTO_INCREMENT ,
`PID`  int(11) NULL DEFAULT NULL ,
`SMS_TO`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`CONTENT`  varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`SMS_LEVEL`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`MEMO`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`TIME`  datetime NULL DEFAULT NULL ,
`APPCODE`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`FLAG`  varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
AUTO_INCREMENT=2151
ROW_FORMAT=Compact

;

-- ----------------------------
-- Table structure for `SMS_RECEIVE`
-- ----------------------------
DROP TABLE IF EXISTS `SMS_RECEIVE`;
CREATE TABLE `SMS_RECEIVE` (
`ID`  int(11) NOT NULL AUTO_INCREMENT ,
`SMS_TO`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`CONTENT`  varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`SMS_LEVEL`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`PRETIME`  datetime NULL DEFAULT NULL ,
`TIME`  datetime NULL DEFAULT NULL ,
`FLAG`  varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`APPCODE`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
ROW_FORMAT=Compact

;

-- ----------------------------
-- Table structure for `SMS_REPEAT`
-- ----------------------------
DROP TABLE IF EXISTS `SMS_REPEAT`;
CREATE TABLE `SMS_REPEAT` (
`ID`  int(11) NOT NULL AUTO_INCREMENT ,
`PID`  int(11) NULL DEFAULT NULL ,
`SMS_TO`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`CONTENT`  varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`SMS_LEVEL`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`TIME`  datetime NULL DEFAULT NULL,
`APPCODE`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
ROW_FORMAT=Compact

;

-- ----------------------------
-- Table structure for `SMS_REPEAT_TEMP`
-- ----------------------------
DROP TABLE IF EXISTS `SMS_REPEAT_TEMP`;
CREATE TABLE `SMS_REPEAT_TEMP` (
`ID`  int(11) NOT NULL AUTO_INCREMENT ,
`HASHCODE`  int(11) NULL DEFAULT NULL ,
`SMS_TO`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`SMS_LEVEL`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`CONTENT`  varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`TIME_START`  datetime NULL DEFAULT NULL ,
`TIME_END`  datetime NULL DEFAULT NULL ,
`APPCODE`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`COUNT_RECORD`  int(11) NULL DEFAULT NULL ,
`FLAG`  varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
ROW_FORMAT=Compact

;

-- ----------------------------
-- Table structure for `SMS_SUCCESS`
-- ----------------------------
DROP TABLE IF EXISTS `SMS_SUCCESS`;
CREATE TABLE `SMS_SUCCESS` (
`ID`  int(11) NOT NULL AUTO_INCREMENT ,
`PID`  int(11) NULL DEFAULT NULL ,
`SMS_TO`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`CONTENT`  varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`SMS_LEVEL`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`TIME`  datetime NULL DEFAULT NULL ,
`APPCODE`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
ROW_FORMAT=Compact

;

-- ----------------------------
-- Table structure for `SMS_TEMP_SAVE`
-- ----------------------------
DROP TABLE IF EXISTS `SMS_TEMP_SAVE`;
CREATE TABLE `SMS_TEMP_SAVE` (
`ID`  int(11) NOT NULL AUTO_INCREMENT ,
`PID`  int(11) NULL DEFAULT NULL ,
`SMS_TO`  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`CONTENT`  varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`SMS_LEVEL`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`FLAG`  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
`TIME`  datetime NULL DEFAULT NULL,
`APPCODE`  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (`ID`)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
ROW_FORMAT=Compact

;


DROP TABLE IF EXISTS `SMS_RECEIVE_TEMP`;
CREATE TABLE `SMS_RECEIVE_TEMP` (
ID  int(11) NOT NULL AUTO_INCREMENT ,
SMS_TO  varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
CONTENT  varchar(300) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
SMS_LEVEL  varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRETIME  datetime NULL DEFAULT NULL ,
TIME  datetime NULL DEFAULT NULL ,
APPCODE  varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PASSRULE VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
OSC VARCHAR(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
FLAG  varchar(2) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL ,
PRIMARY KEY (ID)
)
ENGINE=InnoDB
DEFAULT CHARACTER SET=utf8mb4 COLLATE=utf8mb4_general_ci
ROW_FORMAT=Compact

;



CREATE TABLE `SMS_TOKEN` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `appCode` varchar(100) DEFAULT NULL,
  `token` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

alter table SMS_REPEAT add COLUMN TOKEN varchar(100) DEFAULT NULL;
alter table SMS_REPEAT add COLUMN IP varchar(100) DEFAULT NULL;
alter table SMS_SUCCESS add COLUMN TOKEN varchar(100) DEFAULT NULL;
alter table SMS_SUCCESS add COLUMN IP varchar(100) DEFAULT NULL;
alter table SMS_FAILURE add COLUMN TOKEN varchar(100) DEFAULT NULL;
alter table SMS_FAILURE add COLUMN IP varchar(100) DEFAULT NULL;
alter table SMS_FAILURE add COLUMN REDIS_KEY varchar(100) DEFAULT NULL;

CREATE TABLE `SMS_FAILURE_HISTORY` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `SMS_TO` varchar(200) DEFAULT NULL,
  `CONTENT` varchar(300) DEFAULT NULL,
  `SMS_LEVEL` varchar(10) DEFAULT NULL,
  `MEMO` varchar(5000) DEFAULT NULL,
  `TIME` datetime DEFAULT NULL,
  `APPCODE` varchar(64) DEFAULT NULL,
  `CHANNEL_NO` varchar(20) DEFAULT NULL,
  `TOKEN` varchar(100) DEFAULT NULL,
  `IP` varchar(100) DEFAULT NULL,
  `REDIS_KEY` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 ROW_FORMAT=COMPACT;

#20170601 短信通道发送权重
alter table sms.SMS_CONFIG add weight INT(11) not NULL COMMENT '通道发送权重，1为最大';
alter table sms.SMS_CONFIG add currentWeight INT(11) not NULL COMMENT '当前通道发送权重';
alter table sms.SMS_CONFIG add gmt_create DATETIME not NULL COMMENT '创建时间';
alter table sms.SMS_CONFIG add gmt_modified DATETIME not NULL COMMENT '修改时间';
update sms.SMS_CONFIG set gmt_create = now(), gmt_modified = now();
update sms.SMS_CONFIG set weight = '1', currentWeight = '1' where SMS_CHANNEL = 'XinGe';
update sms.SMS_CONFIG set weight = '2', currentWeight = '2' where SMS_CHANNEL = 'ChuangLan';

#20170610
CREATE TABLE sms.SMS_NOTICE_STATUS (
	`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`msgid` varchar(128) NOT NULL COMMENT '消息id,用于已匹配发送消息',
	`phone` VARCHAR(20) NOT NULL COMMENT '手机号',
	`status_code` varchar(128) DEFAULT NULL COMMENT '状态码',
	`description` varchar(128) DEFAULT NULL COMMENT '描述',
	`channel_no` varchar(20) DEFAULT NULL COMMENT '通道名',
	`gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
	`gmt_receive` datetime DEFAULT NULL COMMENT '状态接受时间',
	PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '通知类送达状态表';

CREATE TABLE sms.SMS_SALE_STATUS (
	`id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
	`msgid` varchar(128) NOT NULL COMMENT '消息id,用于已匹配发送消息',
	`phone` VARCHAR(20) NOT NULL COMMENT '手机号',
	`status_code` varchar(128) DEFAULT NULL COMMENT '状态码',
	`description` varchar(128) DEFAULT NULL COMMENT '描述',
	`channel_no` varchar(20) DEFAULT NULL COMMENT '通道名',
	`gmt_create` datetime DEFAULT NULL COMMENT '创建时间',
	`gmt_receive` datetime DEFAULT NULL COMMENT '状态接受时间',
	PRIMARY KEY (`id`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT '营销类送达状态表';

alter table sms.SMS_SUCCESS add `MSGID` varchar(128) DEFAULT NULL COMMENT '消息id,用于已匹配发送消息';
alter table sms.SMS_SUCCESS comment '通知短信成功表';

CREATE TABLE sms.SMS_SALE_SUCCESS (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `SMS_TO` varchar(20) DEFAULT NULL,
  `CONTENT` varchar(450) DEFAULT NULL,
  `SMS_LEVEL` varchar(10) DEFAULT NULL,
  `TIME` timestamp NULL DEFAULT NULL,
  `APPCODE` varchar(64) DEFAULT NULL,
  `CHANNEL_NO` varchar(20) DEFAULT NULL,
  `TOKEN` varchar(100) DEFAULT NULL,
  `IP` varchar(100) DEFAULT NULL,
  `TYPE` varchar(100) DEFAULT NULL,
  `MSGID` varchar(128) DEFAULT NULL COMMENT '消息id,用于已匹配发送消息',
  PRIMARY KEY (`ID`),
  KEY `time_idx` (`TIME`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='营销短信成功表';

#通知类索引
alter table sms.SMS_SUCCESS add index callbackIndex(msgid,sms_to(191));
alter table sms.SMS_SUCCESS add index msgidIndex(msgid);
alter table sms.SMS_NOTICE_STATUS add index msgidIndex(msgid);
alter table sms.SMS_NOTICE_STATUS add index phoneIndex(phone);
#营销类索引
alter table sms.SMS_SALE_SUCCESS add index callbackIndex(msgid,sms_to);
alter table sms.SMS_SALE_SUCCESS add index msgidIndex(msgid);
alter table sms.SMS_SALE_SUCCESS add index phoneIndex(sms_to);
alter table sms.SMS_SALE_STATUS add index msgidIndex(msgid);
alter table sms.SMS_SALE_STATUS add index phoneIndex(phone);
#2017-06-27
alter table sms.SMS_NOTICE_STATUS add `content` varchar(800) DEFAULT NULL COMMENT '内容';
alter table sms.SMS_NOTICE_STATUS add `type` varchar(100) DEFAULT NULL COMMENT '短信类型';
alter table sms.SMS_NOTICE_STATUS add `appcode` varchar(64) DEFAULT NULL COMMENT '应用表示';
alter table sms.SMS_NOTICE_STATUS add `IP` varchar(100) DEFAULT NULL COMMENT '发送ip';
alter table sms.SMS_NOTICE_STATUS add `gmt_send` datetime DEFAULT NULL COMMENT '发送时间';

alter table sms.SMS_SALE_STATUS add `content` varchar(800) DEFAULT NULL COMMENT '内容';
alter table sms.SMS_SALE_STATUS add `type` varchar(100) DEFAULT NULL COMMENT '短信类型';
alter table sms.SMS_SALE_STATUS add `appcode` varchar(64) DEFAULT NULL COMMENT '应用表示';
alter table sms.SMS_SALE_STATUS add `IP` varchar(100) DEFAULT NULL COMMENT '发送ip';
alter table sms.SMS_SALE_STATUS add `gmt_send` datetime DEFAULT NULL COMMENT '发送时间';
