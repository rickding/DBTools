package jira.tool.report.processor;

import org.apache.poi.ss.usermodel.Cell;

public interface ValueProcessor {
    boolean accept(String header);
    String process(String value, Cell cell);
}
