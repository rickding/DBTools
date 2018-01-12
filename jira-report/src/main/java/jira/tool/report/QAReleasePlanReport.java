package jira.tool.report;

import dbtools.common.utils.DateUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;
import jira.tool.report.processor.HeaderProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;

import java.util.Date;
import java.util.List;

public class QAReleasePlanReport extends BaseReport {
    public QAReleasePlanReport() {
        mapSheetName.put("data", "版本发布会");
        mapSheetName.put("graph", "统计");

        duration = "weekly";
    }

    @Override
    public String getTemplateName() {
        return null; // "版本发布会-template.xlsx";
    }

    @Override
    public String getFileName() {
        return String.format("版本发布会%s.xlsx", DateUtils.format(new Date(), "yyyyMMdd"));
    }

    @Override
    protected List<Story> getStoryList() {
        return DBUtil.getQAStoryList();
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
        // col label
        pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.statusHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, HeaderProcessor.headerList.indexOf(HeaderProcessor.issueKeyHeader));
        // add filter
        pivotTable.addReportFilter(HeaderProcessor.headerList.indexOf(HeaderProcessor.dueDateHeader));
    }
}
