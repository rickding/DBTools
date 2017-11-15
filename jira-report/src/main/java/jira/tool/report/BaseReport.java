package jira.tool.report;

import dbtools.common.utils.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseReport {
    public ReportHeader[] processHeaders(String[] headers) {
        // new ones
        List<ReportHeader> newHeaders = new ArrayList<ReportHeader>(ArrayUtils.isEmpty(headers) ? 16 : headers.length + 16) {{
            add(new ReportHeader("diedai", "到期日"));
        }};

        // Old ones
        ReportHeader[] tmp = ReportHeader.fromStrings(headers);
        newHeaders.addAll(Arrays.asList(tmp));

        // Return the combined ones
        tmp = new ReportHeader[newHeaders.size()];
        newHeaders.toArray(tmp);
        return tmp;
    }

    public String processValue(String header, String value) {
//        if (header)

        return value;
    }
}
