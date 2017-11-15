package jira.tool.report;

import dbtools.common.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public class ReportHeader {
    private String name;
    private String value;

    public ReportHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public static ReportHeader[] fromStrings(String[] headers) {
        List<ReportHeader> newHeaders = new ArrayList<ReportHeader>(ArrayUtils.isEmpty(headers) ? 16 : headers.length + 16);

        if (!ArrayUtils.isEmpty(headers)) {
            for (String header : headers) {
                newHeaders.add(new ReportHeader(header, header));
            }
        }

        ReportHeader[] tmp = new ReportHeader[newHeaders.size()];
        newHeaders.toArray(tmp);
        return tmp;
    }

    public static String[] toStrings(ReportHeader[] headers) {
        List<String> newHeaders = new ArrayList<String>(headers == null ? 16 : headers.length + 16);

        if (headers != null && headers.length > 0) {
            for (ReportHeader header : headers) {
                newHeaders.add(header.getName());
            }
        }

        String[] tmp = new String[newHeaders.size()];
        newHeaders.toArray(tmp);
        return tmp;
    }
}
