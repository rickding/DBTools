package jira.tool.report.processor;

import dbtools.common.utils.StrUtils;

import java.util.Calendar;

public class ResolveDateProcessor extends SprintDateProcessor {
    public ResolveDateProcessor() {
        format = "yyyy/MM/dd HH:mm";
        sprintEnd = Calendar.FRIDAY;
        adjustDelay = false;
    }

    @Override
    public boolean accept(String header) {
        return !StrUtils.isEmpty(header) && header.equalsIgnoreCase(HeaderProcessor.resolveDateHeader.getName());
    }
}
