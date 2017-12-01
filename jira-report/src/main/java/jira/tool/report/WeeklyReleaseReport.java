package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.DateUtils;
import jira.tool.db.JiraUtil;
import jira.tool.db.model.Story;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.TeamProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class WeeklyReleaseReport extends ReleasePlanReport {
    public WeeklyReleaseReport() {
        mapSheetName.put("data", "人天交付运营能力");
        mapSheetName.put("graph", "本周交付统计");
        mapSheetName.put("graph3", "按周交付统计");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph2", "本周交付人均");

        dateProcessor = HeaderProcessor.releaseDateHeader;
        isPlanDate = false;
        fillWholeDate = true;
    }

    @Override
    public String getTemplateName() {
        return null; // "人天交付运营能力-template.xlsx";
    }

    @Override
    public String getFileName() {
        return String.format("人天交付运营能力%s.xlsx", DateUtils.format(new Date(), "MMdd"));
    }

    @Override
    protected List<Story> getStoryList() {
        return JiraUtil.getResolvedStoryList();
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
        pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.teamNameHeader));
        pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.projectHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, HeaderProcessor.headerList.indexOf(HeaderProcessor.issueKeyHeader));
        // add filter
        pivotTable.addReportFilter(HeaderProcessor.headerList.indexOf(HeaderProcessor.releaseDateHeader));

        // Create graph 3
    }

    @Override
    protected void decoratePivotTable2(XSSFPivotTable pivotTable) {
        // Decorate graph
        List<HeaderProcessor> list = Arrays.asList(TeamProcessor.getHeaders());
        pivotTable.addRowLabel(list.indexOf(TeamProcessor.nameHeader));

        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.releaseHeader));
        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.releaseMaxHeader));
//        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.releaseMinHeader));

        pivotTable.addReportFilter(list.indexOf(TeamProcessor.dateHeader));
        pivotTable.addReportFilter(list.indexOf(TeamProcessor.releaseMinHeader));
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
            XSSFPivotTable pivotTable = createPivotTable(graphSheet, dataSheet, HeaderProcessor.headerList.size() - 1);
            if (pivotTable != null) {
                // Decorate graph
                pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.teamNameHeader));
                pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.releaseDateHeader));
                pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, HeaderProcessor.headerList.indexOf(HeaderProcessor.issueKeyHeader));
                pivotTable.addReportFilter(HeaderProcessor.headerList.indexOf(HeaderProcessor.projectHeader));
            } else {
                wb.removeSheetAt(wb.getSheetIndex(graphSheet));
            }
        }
        return sheets;
    }
}
