package jira.tool.report;

import dbtools.common.utils.StrUtils;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.TeamProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ReleasePlanReport extends BaseReport {
    public ReleasePlanReport() {
        mapSheetName.put("data", "未完成开发");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph", "各项目交付节奏表");
        mapSheetName.put("graph2", "人力库存警戒线");
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
        pivotTable.addRowLabel(newHeaders.indexOf(HeaderProcessor.releaseDateHeader));
        // col label
        pivotTable.addRowLabel(newHeaders.indexOf(HeaderProcessor.teamNameHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, newHeaders.indexOf(HeaderProcessor.issueKeyHeader));
        // add filter
        pivotTable.addReportFilter(newHeaders.indexOf(HeaderProcessor.projectHeader));
    }

    private TeamProcessor[] calculateData(XSSFWorkbook wb) {
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

        int dateIndex = newHeaders.indexOf(HeaderProcessor.releaseDateHeader);
        int teamIndex = newHeaders.indexOf(HeaderProcessor.teamNameHeader);

        while (rowStart++ <= rowEnd) {
            Row row = sheet.getRow(rowStart);
            if (row != null) {
                int colStart = row.getFirstCellNum();
                int colEnd = row.getLastCellNum();

                if (dateIndex >= colStart && dateIndex <= colEnd && teamIndex >= colStart && teamIndex <= colEnd) {
                    String date = row.getCell(dateIndex).getStringCellValue();
                    String team = row.getCell(teamIndex).getStringCellValue();

                    // Find the team
                    if (teamProcessors.containsKey(team)) {
                        teamProcessors.get(team).countStory(date);
                    }
                }
            }
        }

        TeamProcessor[] tmp = new TeamProcessor[teamProcessors.size()];
        teamProcessors.values().toArray(tmp);
        return tmp;
    }

    /**
     * Fill data sheets
     *
     * @return
     */
    @Override
    public XSSFSheet[] fillDataSheets(XSSFWorkbook wb) {
        if (wb == null) {
            return null;
        }

        // Calculate the data
        TeamProcessor[] teams = calculateData(wb);

        // Fill the team data
        String sheetName = getSheetName("graph2");
        XSSFSheet graphSheet = StrUtils.isEmpty(sheetName) ? wb.createSheet() : wb.createSheet(sheetName);

        sheetName = getSheetName("data2");
        XSSFSheet dataSheet = StrUtils.isEmpty(sheetName) ? wb.createSheet() : wb.createSheet(sheetName);

        // Headers
        int row = 0;
        HeaderProcessor[] headers = TeamProcessor.getHeaders();
        ExcelUtil.fillRow(dataSheet, row++, HeaderProcessor.toStrings(headers));

        // Data
        for (TeamProcessor team : teams) {
            row += team.fillRow(dataSheet, row);
        }

        // Decorate data
        decorateDataSheet(dataSheet);

        // Pivot table
        XSSFPivotTable pivotTable = createPivotTable(graphSheet, dataSheet, TeamProcessor.getHeaders().length - 1);

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
        return new XSSFSheet[]{dataSheet, graphSheet};
    }
}
