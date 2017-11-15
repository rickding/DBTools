package jira.tool.report;

public class SprintReport extends BaseReport {
    public SprintReport() {
        mapSheetName.put("data", "未完成开发");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph", "各项目交付节奏表");
        mapSheetName.put("graph2", "人力库存警戒线");
    }
}
