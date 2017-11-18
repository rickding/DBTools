package jira.tool.report;

public class DailyDevFinishReport extends BaseReport {
    public DailyDevFinishReport() {
        mapSheetName.put("data", "完成开发待提测");
        mapSheetName.put("graph", "graph");
    }

    @Override
    public String getTemplateName() {
        return null; // "完成开发待提测-template.xlsx";
    }
}
