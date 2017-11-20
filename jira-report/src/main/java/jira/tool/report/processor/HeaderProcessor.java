package jira.tool.report.processor;

import dbtools.common.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class HeaderProcessor {
    public static HeaderProcessor dueDateHeader = new HeaderProcessor("Due Date", "到期日");
    public static HeaderProcessor resolveDateHeader = new HeaderProcessor("Resolve Date", "解决");
    public static HeaderProcessor startDateHeader = new HeaderProcessor("Start Date", "Custom field (计划开始日期)");
    public static HeaderProcessor teamKeyHeader = new HeaderProcessor("Team Key", "Project key");
    public static HeaderProcessor teamNameHeader = new HeaderProcessor("Team Name", "Project name");
    public static HeaderProcessor issueKeyHeader = new HeaderProcessor("Issue Key", "Issue key");
    public static HeaderProcessor projectHeader = new HeaderProcessor("Project", "Custom field (所属项目)");
    public static HeaderProcessor estimationHeader = new HeaderProcessor("Estimation Man-day", "剩余时间");

    private String name;
    private String value;

    public HeaderProcessor(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    // Create the header object from string arrays
    public static HeaderProcessor[] fromStrings(String[] headers) {
        List<HeaderProcessor> newHeaders = new ArrayList<HeaderProcessor>(ArrayUtils.isEmpty(headers) ? 16 : headers.length + 16);

        if (!ArrayUtils.isEmpty(headers)) {
            for (String header : headers) {
                newHeaders.add(new HeaderProcessor(header, header));
            }
        }

        HeaderProcessor[] tmp = new HeaderProcessor[newHeaders.size()];
        newHeaders.toArray(tmp);
        return tmp;
    }

    // Return the array of header names
    public static String[] toStrings(HeaderProcessor[] headers) {
        List<String> newHeaders = new ArrayList<String>(headers == null ? 16 : headers.length + 16);

        if (headers != null && headers.length > 0) {
            for (HeaderProcessor header : headers) {
                newHeaders.add(header.getName());
            }
        }

        String[] tmp = new String[newHeaders.size()];
        newHeaders.toArray(tmp);
        return tmp;
    }
}
