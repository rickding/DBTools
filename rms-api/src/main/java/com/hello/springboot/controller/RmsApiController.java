package com.hello.springboot.controller;

import jira.tool.report.RMSUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by user on 2017/5/28.
 */
@RestController
@EnableAutoConfiguration
public class RmsApiController {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index() {
        return "Hello RMS API";
    }

    @RequestMapping(value = "/chk", method = RequestMethod.GET)
    public String chk() {
        return "ok";
    }

    @RequestMapping(value = "/report", method = RequestMethod.GET)
    public String report() {
        jira.tool.report.App.main(null);
        return String.format("ok, %s", RMSUtil.getClassUrl());
    }
}
