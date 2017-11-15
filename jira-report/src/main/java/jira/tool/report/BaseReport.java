package jira.tool.report;

import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.ProjectNameProcessor;
import jira.tool.report.processor.ReleaseDateProcessor;
import jira.tool.report.processor.ValueProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseReport {
    // Configure the processors
    private List<ValueProcessor> valueProcessors = new ArrayList<ValueProcessor>() {{
        add(new ProjectNameProcessor());
        add(new ReleaseDateProcessor());
    }};

    // Configure the headers
    List<HeaderProcessor> newHeaders = new ArrayList<HeaderProcessor>() {{
        add(HeaderProcessor.releaseDateHeader);
    }};

    // Combine the headers
    public HeaderProcessor[] processHeaders(String[] headers) {
        // new ones
        List<HeaderProcessor> allHeaders = new ArrayList<HeaderProcessor>() {{
            addAll(newHeaders);
        }};

        // Old ones
        HeaderProcessor[] tmp = HeaderProcessor.fromStrings(headers);
        allHeaders.addAll(Arrays.asList(tmp));

        // Return the combined ones
        tmp = new HeaderProcessor[allHeaders.size()];
        allHeaders.toArray(tmp);
        return tmp;
    }

    // Process and return the new value
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
