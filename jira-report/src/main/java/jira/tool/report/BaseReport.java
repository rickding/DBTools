package jira.tool.report;

import dbtools.common.utils.ArrayUtils;
import jira.tool.report.processor.ProjectNameProcessor;
import jira.tool.report.processor.ValueProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseReport {
    // Configure the processors
    private List<ValueProcessor> valueProcessors = new ArrayList<ValueProcessor>(){{
        add(new ProjectNameProcessor());
    }};

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
        if (valueProcessors == null || valueProcessors.size() <= 0) {
            return value;
        }

        // Call the processors
        for (ValueProcessor valueProcessor : valueProcessors) {
            if (valueProcessor.accept(header)) {
                value = valueProcessor.process(value);
            }
        }
        return value;
    }
}
