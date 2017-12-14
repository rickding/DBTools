package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.TeamProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReleasePlanReport extends BaseReport {
    protected HeaderProcessor dateProcessor = HeaderProcessor.dueDateHeader;
    protected boolean isPlanDate = true;
    protected boolean fillWholeDate = false;

    public ReleasePlanReport() {
        mapSheetName.put("data", "未完成开发");
        mapSheetName.put("graph", "各项目交付节奏表");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph2", "人力库存警戒线");
    }

    @Override
    public String getTemplateName() {
        return null; // "未完成开发-4周内-交付计划-template.xlsx";
    }

    @Override
    public String getFileName() {
        return String.format("未完成开发-4周内-交付计划-%s.xlsx", DateUtils.format(new Date(), "MMdd"));
    }

    @Override
    protected List<Story> getStoryList() {
        return DBUtil.getUnDevelopedStoryList();
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
        pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.dueDateHeader));
        // col label
        pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.teamNameHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, HeaderProcessor.headerList.indexOf(HeaderProcessor.issueKeyHeader));
        // add filter
        pivotTable.addReportFilter(HeaderProcessor.headerList.indexOf(HeaderProcessor.projectHeader));
    }

    protected TeamProcessor[] calculateData(XSSFWorkbook wb) {
        if (wb == null) {
            return null;
        }

        // Get sheet for the data
        XSSFSheet sheet = wb.getSheet(getSheetName("data"));
        if (sheet == null) {
            return null;
        }

        // Create the team processors
        Map<String, TeamProcessor> teamProcessors = TeamProcessor.createTeamProcessors();
        if (teamProcessors == null || teamProcessors.size() <= 0) {
            return null;
        }

        // Iterate the data
        int rowStart = sheet.getFirstRowNum();
        int rowEnd = sheet.getLastRowNum();
        rowStart++; // Skip headers

        int dateIndex = HeaderProcessor.headerList.indexOf(dateProcessor);
        int teamIndex = HeaderProcessor.headerList.indexOf(HeaderProcessor.teamNameHeader);
        int timeIndex = HeaderProcessor.headerList.indexOf(HeaderProcessor.estimationHeader);

        while (rowStart <= rowEnd) {
            Row row = sheet.getRow(rowStart);
            if (row != null) {
                int colStart = row.getFirstCellNum();
                int colEnd = row.getLastCellNum();

                if (dateIndex >= colStart && dateIndex <= colEnd && teamIndex >= colStart && teamIndex <= colEnd) {
                    String date = row.getCell(dateIndex).getStringCellValue();
                    String team = row.getCell(teamIndex).getStringCellValue();
                    double time = 0.0;
                    try {
                        time = row.getCell(timeIndex).getNumericCellValue();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Find the team
                    if (!StrUtils.isEmpty(team)) {
                        team = team.toLowerCase();
                        if (teamProcessors.containsKey(team)) {
                            TeamProcessor processor = teamProcessors.get(team);
                            processor.countStory(date, time);
                        }
                    }
                }
            }
            rowStart++;
        }

        TeamProcessor[] tmp = new TeamProcessor[teamProcessors.size()];
        teamProcessors.values().toArray(tmp);

        // Check the whole dates and fill them
        if (fillWholeDate) {
            TeamProcessor.fillWholeDate(tmp);
        }
        return tmp;
    }

    /**
     * Fill data sheets
     *
     * @return
     */
    @Override
    public XSSFSheet[] fillDataSheets(XSSFWorkbook wb) {
        XSSFSheet[] sheets = super.fillDataSheets(wb);
        if (wb == null || isTemplateUsed()) {
            return sheets;
        }

        // Calculate the data
        TeamProcessor[] teams = calculateData(wb);

        // Fill the team data
        XSSFSheet dataSheet = ExcelUtil.getOrCreateSheet(wb, getSheetName("data2"));

        // Headers
        int row = 0;
        HeaderProcessor[] headers = TeamProcessor.getHeaders();
        TeamProcessor.dateHeader.setName(dateProcessor.getName());
        ExcelUtil.fillRow(dataSheet, row++, HeaderProcessor.toStrings(headers));

        // Data
        for (TeamProcessor team : teams) {
            row += team.fillRow(dataSheet, row, isPlanDate);
        }

        if (!isTemplateUsed()) {
            // Decorate data
            decorateDataSheet(dataSheet);

            // Pivot table
            XSSFSheet graphSheet = ExcelUtil.getOrCreateSheet(wb, getSheetName("graph2"));
            XSSFPivotTable pivotTable = createPivotTable(graphSheet, dataSheet, TeamProcessor.getHeaders().length - 1);
            if (pivotTable != null) {
                decoratePivotTable2(pivotTable);
            } else {
                wb.removeSheetAt(wb.getSheetIndex(graphSheet));
            }
        }
        return new XSSFSheet[]{dataSheet};
    }

    protected void decoratePivotTable2(XSSFPivotTable pivotTable) {
        if (pivotTable == null) {
            return;
        }

        // Decorate graph
        // row label
        List<HeaderProcessor> list = Arrays.asList(TeamProcessor.getHeaders());
        pivotTable.addRowLabel(list.indexOf(TeamProcessor.dateHeader));
        pivotTable.addRowLabel(list.indexOf(TeamProcessor.nameHeader));

        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.releaseMaxHeader));
        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.releaseHeader));
//        pivotTable.addColumnLabel(DataConsolidateFunction.SUM, list.indexOf(TeamProcessor.releaseMinHeader));

        pivotTable.addReportFilter(list.indexOf(TeamProcessor.releaseMinHeader));
    }
}
