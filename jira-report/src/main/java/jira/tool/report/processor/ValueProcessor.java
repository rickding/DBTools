package jira.tool.report.processor;

public interface ValueProcessor {
    boolean accept(String header);
    String process(String value);
}
