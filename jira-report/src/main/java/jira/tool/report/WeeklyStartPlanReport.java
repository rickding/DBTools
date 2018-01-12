package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.DateUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.SprintDateProcessor;
import jira.tool.report.processor.TeamProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklyStartPlanReport extends ReleasePlanReport {
    public WeeklyStartPlanReport() {
        mapSheetName.put("data", "计划开始");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph2", "story个数统计");
        mapSheetName.put("graph3", "story估时统计");

        duration = "weekly";
        dateProcessor = HeaderProcessor.startDateHeader;
        isPlanDate = false;
        fillWholeDate = true;
    }

    @Override
    public String getTemplateName() {
        return useTemplate ? "template-计划开始.xlsx" : null;
    }

    @Override
    public String getFileName() {
        return String.format("计划开始-%s.xlsx", DateUtils.format(new Date(), "MMdd"));
    }

    @Override
    protected List<Story> getStoryList() {
        return DBUtil.getStartPlanStoryList();
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

    @Override
    protected void postToRms2(List<Map<String, String>> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        // Manipulate the data: Release Date, Team Name
        Map<String, Map<String, Integer>> dataMap = new HashMap<String, Map<String, Integer>>();
        Map<String, Map<String, Double>> timeTeamMap = new HashMap<String, Map<String, Double>>();
        Map<String, Map<String, Integer>> manDayTeamMap = new HashMap<String, Map<String, Integer>>();

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

            // Find the team
            if (!dataMap.containsKey(releaseDate)) {
                dataMap.put(releaseDate, new HashMap<String, Integer>());
            }
            Map<String, Integer> teamMap = dataMap.get(releaseDate);
            String value = record.get(TeamProcessor.storyHeader.getName());
            Integer count = Integer.valueOf(value);
            if (teamMap.containsKey(team)) {
                count += teamMap.get(team);
            }
            teamMap.put(team, count);

            // Time
            if (!timeTeamMap.containsKey(releaseDate)) {
                timeTeamMap.put(releaseDate, new HashMap<String, Double>());
            }
            Map<String, Double> timeMap = timeTeamMap.get(releaseDate);

            value = record.get(TeamProcessor.timeHeader.getName());
            Double dbl = Double.valueOf(value);
            if (timeMap.containsKey(team)) {
                dbl += timeMap.get(team);
            }
            timeMap.put(team, dbl);

            // Manday
            if (!manDayTeamMap.containsKey(releaseDate)) {
                manDayTeamMap.put(releaseDate, new HashMap<String, Integer>());
            }
            teamMap = manDayTeamMap.get(releaseDate);

            value = record.get(TeamProcessor.manDayHeader.getName());
            count = Integer.valueOf(value);
            if (teamMap.containsKey(team)) {
                count += teamMap.get(team);
            }
            teamMap.put(team, count);
        }

        if (dataMap == null || dataMap.size() <= 0 || minReleaseDate == null || !dataMap.containsKey(minReleaseDate)) {
            System.out.printf("Error when postToRms2: %s\r\n", records.toString());
            return;
        }
        final Map<String, Integer> teamMap = dataMap.get(minReleaseDate);
        final Map<String, Double> timeMap = timeTeamMap.get(minReleaseDate);
        final Map<String, Integer> mandayMap = manDayTeamMap.get(minReleaseDate);

        // Format: Start Date, Team Name
//        const data = [
//            { 'APP': 18.9, '财务线': 28.8, '导购线': 39.3, 'Apr.': 81.4, 'May': 47, 'Jun.': 20.3, 'Jul.': 24, 'Aug.': 35.6 }
//        ];

        List<Map<String, Object>> chartDataList = new ArrayList<Map<String, Object>>();
        chartDataList.add(new HashMap<String, Object>() {{
            put("name", "story个数统计");
            for (final Map.Entry<String, Integer> team : teamMap.entrySet()) {
                put(team.getKey(), team.getValue());
            }
        }});
        RMSUtil.postReport(String.format("%s_%s", getName(), getSheetName("graph2")), dateStr, duration, chartDataList);

        // Format: Estimation/Man-day, Team Name
//        const data = [
//                { name: 'Story估时人天', 'APP': 18.9, '财务线': 28.8, '导购线': 39.3, 'Apr.': 81.4, 'May': 47, 'Jun.': 20.3, 'Jul.': 24, 'Aug.': 35.6 },
//                { name: '库存人天', 'APP': 12.4, '财务线.': 23.2, '导购线': 34.5, 'Apr.': 99.7, 'May': 52.6, 'Jun.': 35.5, 'Jul.': 37.4, 'Aug.': 42.4 }
//        ];

        chartDataList = new ArrayList<Map<String, Object>>();
        chartDataList.add(new HashMap<String, Object>() {{
            put("name", "Story估时人天");
            for (final Map.Entry<String, Double> team : timeMap.entrySet()) {
                put(team.getKey(), team.getValue());
            }
        }});
        chartDataList.add(new HashMap<String, Object>() {{
            put("name", "库存人天");
            for (final Map.Entry<String, Integer> team : mandayMap.entrySet()) {
                put(team.getKey(), team.getValue());
            }
        }});
        RMSUtil.postReport(String.format("%s_%s", getName(), getSheetName("graph3")), dateStr, duration, chartDataList);
    }
}
