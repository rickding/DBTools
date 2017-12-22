package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.SprintDateProcessor;
import jira.tool.report.processor.TeamEnum;
import jira.tool.report.processor.TeamProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
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

        duration = "half-weekly";
        fillWholeDate = true;
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
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();
        for (TeamProcessor team : teams) {
            row += team.fillRow(dataSheet, row, isPlanDate);
            records.addAll(team.getNameValueMap(isPlanDate));
        }

        // Post to rms
        postToRms2(records);

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

    protected void postToRms2(List<Map<String, String>> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        // Manipulate the data: Release Date, Team Name
        Map<String, Map<String, Double>> dataMap = new HashMap<String, Map<String, Double>>();
        String minReleaseDate = null;
        String strToday = DateUtils.format(SprintDateProcessor.getSprintDate(new Date(), true), "yyyy-MM-dd");

        for (Map<String, String> record : records) {
            String releaseDate = record.get(TeamProcessor.dateHeader.getName());
            String team = record.get(TeamProcessor.nameHeader.getName());

            if (strToday.compareTo(releaseDate) < 0) {
                if (minReleaseDate == null || minReleaseDate.compareTo(releaseDate) > 0){
                    minReleaseDate = releaseDate;
                }
            }

            // Find the project
            if (!dataMap.containsKey(releaseDate)) {
                dataMap.put(releaseDate, new HashMap<String, Double>());
            }
            Map<String, Double> teamMap = dataMap.get(releaseDate);

            // Value
            String value = record.get(TeamProcessor.releaseHeader.getName());
            Double dbl = Double.valueOf(value);
            if (teamMap.containsKey(team)) {
                dbl += teamMap.get(team);
            }
            teamMap.put(team, dbl);
        }

        // Format: Project, Team Name
//        const data = [
//            { 'team': 'APP', Current: 1.0, Max: 4, Min: 2 },
//            { 'team': '基础架构', Current: 4.6, Max: 4, Min: 2 }
//        ];


        // Only 4 weeks from current
        String[] dates = new String[dataMap.keySet().size()];
        dataMap.keySet().toArray(dates);
        Arrays.sort(dates);

        int count = 0;
        for (String date : dates) {
            if (minReleaseDate.compareTo(date) > 0) {
                continue;
            }
            count++;
            if (count > 4) {
                break;
            }

            Map<String, Double> teamMap = dataMap.get(date);
            List<Map<String, Object>> chartDataList = new ArrayList<Map<String, Object>>();
            for (final Map.Entry<String, Double> team : teamMap.entrySet()) {
                chartDataList.add(new HashMap<String, Object>() {{
                    put("team", team.getKey());
                    put("Current", team.getValue());
                    put("Max", TeamEnum.APP.getReleaseMax());
                    put("Min", TeamEnum.APP.getReleaseMin());
                }});
            }
            RMSUtil.postReport(String.format("%s_%s_第%d周", getName(), getSheetName("graph2"), count), dateStr, duration, chartDataList);
        }
    }
}
