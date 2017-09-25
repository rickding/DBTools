package db2ea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SqlParserTest {
    @Test
    public void testParse() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put(null, null);
            put("", null);

            // xcd
            put("/*", null);
            put("Navicat MySQL Data Transfer", null);
            put("Source Server         : aliyundb2.0", null);

            put("Source Database       : oms-v2-st", ",\"oms-v2-st\",Artifact,database connection,1_oms-v2-st,");
            put("Date: 2017-09-22 17:13:25", null);
            put("*/", null);
            put("SET FOREIGN_KEY_CHECKS=0;", null);
            put("-- ----------------------------", null);
            put("-- Table structure for agif_agent", null);
            put("DROP TABLE IF EXISTS `agif_agent`;", null);

            put("CREATE TABLE `agif_agent` (", ",\"agif_agent\",Class,table,2_agif_agent,");
            put("`AGENT_ID` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '买手ID',", ",\"AGENT_ID买手ID\",Class,function,3_AGENT_ID,");

            put("PRIMARY KEY (`TSP_STD_ID`),", null);
            put(") ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;", null);

            // ody: prod
            put("-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)", null);
            put("--", null);

            put("-- Host: 192.168.1.140    Database: ad", ",\"ad\",Artifact,database connection,1_ad,");
            put("-- ------------------------------------------------------", null);
            put("-- Server version\t5.6.29", null);
            put("/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;", null);
            put("-- Table structure for table `ad_code`", null);
            put("DROP TABLE IF EXISTS `ad_code`;", null);

            put("CREATE TABLE `ad_code` (", ",\"ad_code\",Class,table,2_ad_code,");
//            put("`id` bigint(20) NOT NULL AUTO_INCREMENT,", "id,Class,function,3_id,");
            put("`id` bigint(20) NOT NULL AUTO_INCREMENT,", null);
            put("`page_type` bigint(20) NOT NULL COMMENT '广告,页面',", ",\"page_type广告,页面\",Class,function,3_page_type,");

            put("PRIMARY KEY (`id`),", null);
            put("KEY `index_adcode_page` (`page_type`,`code`,`is_deleted`),", null);
            put(") ENGINE=InnoDB AUTO_INCREMENT=1194020000000001 DEFAULT CHARSET=utf8 ROW_FORMAT=COMPACT;", null);
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            EAItem item = SqlParser.parse(io.getKey(), null);
            String ret = item == null ? null : item.toString();
            Assert.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testParseName() {
        Map<Object[], String> mapIO = new HashMap<Object[], String>() {{
            put(new Object[] {null, null, 0, null}, "");
            put(new Object[] {"name", null, 0, null}, "name");
            put(new Object[] {"create table ea", " ", 2, null}, "ea");
            put(new Object[] {"create table `ea`", " ", 2, new String[] {"`"}}, "ea");

            put(new Object[] {"Database: ad", SqlParser.DB_Splitter, SqlParser.DB_Index, SqlParser.DB_Trim_List}, "ad");
            put(new Object[] {"Database       : oms-v2-st", SqlParser.DB_Splitter, SqlParser.DB_Index, SqlParser.DB_Trim_List}, "oms-v2-st");

            put(new Object[] {"CREATE TABLE `agif_agent` (", SqlParser.Table_Splitter, SqlParser.Table_Index, SqlParser.Table_Trim_List}, "agif_agent");
            put(new Object[] {"CREATE TABLE `ad_code` (", SqlParser.Table_Splitter, SqlParser.Table_Index, SqlParser.Table_Trim_List}, "ad_code");

            put(new Object[] {"`id` bigint(20) NOT NULL AUTO_INCREMENT,", SqlParser.Field_Splitter, SqlParser.Field_Index, SqlParser.Field_Trim_List}, "id");
            put(new Object[] {"`AGENT_ID` varchar(36) COLLATE utf8_unicode_ci NOT NULL COMMENT '买手ID',", SqlParser.Field_Splitter, SqlParser.Field_Index, SqlParser.Field_Trim_List}, "AGENT_ID");

            put(new Object[]{"COMMENT '广告页面',", SqlParser.Field_Comment_Splitter, SqlParser.Comment_Index, SqlParser.Comment_Trim_List}, "广告页面");
            put(new Object[]{"COMMENT '买手ID',", SqlParser.Field_Comment_Splitter, SqlParser.Comment_Index, SqlParser.Comment_Trim_List}, "买手ID");
            put(new Object[]{" COMMENT='客户表';", SqlParser.Table_Comment_Splitter, SqlParser.Comment_Index, SqlParser.Comment_Trim_List}, "客户表");
        }};

        for (Map.Entry<Object[], String> io : mapIO.entrySet()) {
            Object[] params = io.getKey();
            String ret = SqlParser.parseName((String) params[0], (String) params[1], (Integer) params[2], (String[]) params[3], null);
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
