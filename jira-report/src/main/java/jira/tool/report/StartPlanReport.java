package jira.tool.report;

import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.TeamEnum;
import jira.tool.report.processor.TeamProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class StartPlanReport extends ReleasePlanReport {
    public StartPlanReport() {
        mapSheetName.put("data", "计划开始");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph2", "story个数统计");
        mapSheetName.put("graph3", "story估时统计");
    }

    @Override
    public String getTemplateName() {
        return null; // "计划开始-template.xlsx";
    }

    @Override
    protected XSSFPivotTable createPivotTable(XSSFSheet graphSheet, XSSFSheet dataSheet, int maxColIndex) {
        if (mapSheetName.get("graph").equalsIgnoreCase(graphSheet.getSheetName())) {
            return null;
        }
        return super.createPivotTable(graphSheet, dataSheet, maxColIndex);
    }

    @Override
    protected void decoratePivotTable2(XSSFPivotTable pivotTable) {
        // Decorate graph
        List<HeaderProcessor> list = Arrays.asList(TeamProcessor.getHeaders());
        pivotTable.addRowLabel(list.indexOf(TeamProcessor.nameHeader));

        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.storyHeader));

        pivotTable.addReportFilter(list.indexOf(TeamProcessor.dateHeader));
    }

    @Override
    public XSSFSheet[] fillDataSheets(XSSFWorkbook wb) {
        XSSFSheet[] sheets = super.fillDataSheets(wb);
        if (wb == null || isTemplateUsed()) {
            return sheets;
        }

        // Pivot table3
        XSSFSheet dataSheet = wb.getSheet(mapSheetName.get("data2"));
        if (dataSheet != null) {
            XSSFSheet graphSheet = ExcelUtil.getOrCreateSheet(wb, getSheetName("graph3"));
            XSSFPivotTable pivotTable = createPivotTable(graphSheet, dataSheet, TeamProcessor.getHeaders().length - 1);
            if (pivotTable != null) {
                // Decorate graph
                List<HeaderProcessor> list = Arrays.asList(TeamProcessor.getHeaders());
                pivotTable.addRowLabel(list.indexOf(TeamProcessor.nameHeader));

                pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.timeHeader));
                pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.manDayHeader));

                pivotTable.addReportFilter(list.indexOf(TeamProcessor.dateHeader));
            } else {
                wb.removeSheetAt(wb.getSheetIndex(graphSheet));
            }
        }
        return sheets;
    }
}
