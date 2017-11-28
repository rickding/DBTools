package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.TeamEnum;
import jira.tool.report.processor.TeamProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.List;

public class WeeklyReleasePlanReport extends ReleasePlanReport {
    public WeeklyReleasePlanReport() {
        mapSheetName.put("data", "计划交付");
        mapSheetName.put("graph", "客户统计");
        mapSheetName.put("graph3", "团队统计");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph2", "人天统计");

        fillWholeDate = true;
    }

    @Override
    public String getTemplateName() {
        return null; // "计划交付-template.xlsx";
    }

    /**
     * configure the pivot table
     *
     * @param pivotTable
     */
    @Override
    public void decoratePivotTable(XSSFPivotTable pivotTable) {
        if (pivotTable == null) {
            return;
        }

        // configure the pivot table
        // row label
        pivotTable.addRowLabel(newHeaders.indexOf(HeaderProcessor.projectHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, newHeaders.indexOf(HeaderProcessor.issueKeyHeader));

        // Set the graph sheet name with the value
        Sheet sheet = pivotTable.getDataSheet();
        if (sheet != null) {
            int count = sheet.getLastRowNum() - sheet.getFirstRowNum();
            double value = 1 - (double) count / (7 * 4000);
            String str = String.format("复用率1-%d div(7x4000)=%.4f", count, value * 100);
            ExcelUtil.getOrCreateSheet((XSSFWorkbook) sheet.getWorkbook(), str);
        }
    }

    @Override
    public void decoratePivotTable2(XSSFPivotTable pivotTable) {
        if (pivotTable == null || isTemplateUsed()) {
            return;
        }

        List<HeaderProcessor> headers = Arrays.asList(TeamProcessor.getHeaders());
        // configure the pivot table
        // row label
        pivotTable.addRowLabel(headers.indexOf(TeamProcessor.nameHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, headers.indexOf(TeamProcessor.timeHeader));
        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, headers.indexOf(TeamProcessor.manDayHeader));

        // Set the graph sheet name with the value
        XSSFWorkbook wb = (XSSFWorkbook) pivotTable.getParentSheet().getWorkbook();
        Sheet sheet = wb.getSheet(mapSheetName.get("data"));
        if (sheet != null) {
            int count = sheet.getLastRowNum() - sheet.getFirstRowNum();
            int member = TeamEnum.getTotalMember();
            int days = TeamProcessor.getWorkDays(calculateData(wb));
            double value = (double) count / member / days;
            String str = String.format("人均%d div %d div %d=%.4f", count, member, days, value);
            ExcelUtil.getOrCreateSheet((XSSFWorkbook) sheet.getWorkbook(), str);
        }
    }

    @Override
    public XSSFSheet[] fillDataSheets(XSSFWorkbook wb) {
        XSSFSheet[] sheets = super.fillDataSheets(wb);
        if (wb == null || isTemplateUsed()) {
            return sheets;
        }

        // Pivot table3
        XSSFSheet dataSheet = wb.getSheet(mapSheetName.get("data"));
        if (dataSheet != null) {
            XSSFSheet graphSheet = ExcelUtil.getOrCreateSheet(wb, getSheetName("graph3"));
            XSSFPivotTable pivotTable = createPivotTable(graphSheet, dataSheet, newHeaders.size() - 1);
            if (pivotTable != null) {
                // Decorate graph
                pivotTable.addRowLabel(newHeaders.indexOf(HeaderProcessor.teamNameHeader));
                pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, newHeaders.indexOf(HeaderProcessor.issueKeyHeader));
                pivotTable.addReportFilter(newHeaders.indexOf(HeaderProcessor.dueDateHeader));
            } else {
                wb.removeSheetAt(wb.getSheetIndex(graphSheet));
            }
        }
        return sheets;
    }
}
