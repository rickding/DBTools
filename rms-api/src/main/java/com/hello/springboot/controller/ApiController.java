package com.hello.springboot.controller;

import com.alibaba.fastjson.JSONObject;
import dbtools.common.JsonUtil;
import jira.tool.report.RMSUtil;
import jira.tool.report.WeeklyReleaseReport;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2017/5/28.
 */
@RestController
@EnableAutoConfiguration
public class ApiController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "Hello RMS API";
    }

    @RequestMapping(value = "/chk", method = RequestMethod.GET)
    public JSONObject chk() {
        System.out.printf("ok\n");
        return new JSONObject(new HashMap<String, Object>() {{
            put("msg", "ok");
        }});
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public Map<String, String> report() {
        jira.tool.report.App.main(null);
        return new HashMap<String, String>() {{
            put("msg", "ok");
            put("url", RMSUtil.getClassUrl());
        }};
    }

    @RequestMapping(value = "/weekly_release", method = RequestMethod.GET)
    public String weeklyRelease() {
        WeeklyReleaseReport report = new WeeklyReleaseReport();
        XSSFWorkbook wb = report.getWorkbook(null);

        List<String[]> recordList = new ArrayList<String[]>() {{
            add(new String[]{"aa", "bb"});
            add(new String[]{"aa2", "bb2"});
        }};
        report.fillDataSheets(wb);
        return JsonUtil.toString(recordList);
    }
}
