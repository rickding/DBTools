package jira.tool.report.processor;

import dbtools.common.utils.StrUtils;

import java.util.Calendar;

public class StartDateProcessor extends SprintDateProcessor {
    public StartDateProcessor() {
        format = "yyyy/MM/dd HH:mm a";
        sprintEnd = Calendar.SATURDAY;
        adjustDelay = false;
    }

    @Override
    public boolean accept(String header) {
        return !StrUtils.isEmpty(header) && header.equalsIgnoreCase(HeaderProcessor.startDateHeader.getName());
    }
}
