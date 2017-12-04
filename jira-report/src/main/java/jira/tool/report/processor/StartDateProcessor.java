package jira.tool.report.processor;

import java.util.Calendar;

public class StartDateProcessor extends SprintDateProcessor {
    public StartDateProcessor() {
        sprintEnd = Calendar.SATURDAY;
        adjustDelay = false;

        acceptedHeaderList.clear();
        acceptedHeaderList.add(HeaderProcessor.startDateHeader);
        acceptedHeaderList.add(HeaderProcessor.resolveDateHeader);
        acceptedHeaderList.add(HeaderProcessor.releaseDateHeader);
    }
}
