package jira.tool.report;

public class DailyDueDateReport extends BaseReport {
    public DailyDueDateReport() {
        mapSheetName.put("data", "到期日没有或超过4周");
        mapSheetName.put("graph", "graph");
    }

    @Override
    public String getTemplateName() {
        return null; // "到期日没有或超过4周-template.xlsx";
    }
}
