package jira.tool.report;

public class NoDueDateReport extends BaseReport {
    public NoDueDateReport() {
        mapSheetName.put("data", "到期日没有或超过4周");
        mapSheetName.put("graph", "graph");
    }
}
