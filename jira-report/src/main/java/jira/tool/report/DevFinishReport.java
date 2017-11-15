package jira.tool.report;

public class DevFinishReport extends BaseReport {
    public DevFinishReport() {
        mapSheetName.put("data", "完成开发待提测");
        mapSheetName.put("graph", "graph");
    }
}
