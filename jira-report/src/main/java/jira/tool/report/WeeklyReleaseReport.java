package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.DateUtils;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.TeamEnum;
import jira.tool.report.processor.TeamNameProcessor;
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

public class WeeklyReleaseReport extends ReleasePlanReport {
    public WeeklyReleaseReport() {
        mapSheetName.put("data", "人天交付运营能力");
        mapSheetName.put("graph", "本周交付统计");
        mapSheetName.put("graph3", "按周交付统计");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph2", "本周交付人均");

        duration = "weekly";
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
        return DBUtil.getReleasedStoryList();
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

    @Override
    protected void postToRms(List<Map<String, String>> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        // Manipulate the data: Release Date, Project, Team Name
        Map<String, Map<String, Map<String, Integer>>> dataMap = new HashMap<String, Map<String, Map<String, Integer>>>();
        String maxReleaseDate = null;
        for (Map<String, String> record : records) {
            String releaseDate = record.get(HeaderProcessor.releaseDateHeader.getName());
            String project = record.get(HeaderProcessor.projectHeader.getName());
            String team = record.get(HeaderProcessor.teamNameHeader.getName());

            if (maxReleaseDate == null || maxReleaseDate.compareTo(releaseDate) < 0) {
                maxReleaseDate = releaseDate;
            }

            // Find the project
            if (!dataMap.containsKey(releaseDate)) {
                dataMap.put(releaseDate, new HashMap<String, Map<String, Integer>>());
            }
            Map<String, Map<String, Integer>> projectTeamMap = dataMap.get(releaseDate);

            // Find the team
            if (!projectTeamMap.containsKey(project)) {
                projectTeamMap.put(project, new HashMap<String, Integer>());
            }
            Map<String, Integer> teamMap = projectTeamMap.get(project);

            // Count
            teamMap.put(team, teamMap.containsKey(team) ? (teamMap.get(team) + 1) : 1);
        }

        if (dataMap == null || dataMap.size() <= 0 || maxReleaseDate == null || !dataMap.containsKey(maxReleaseDate)) {
            System.out.printf("Error when postToRms: %s\r\n", records.toString());
            return;
        }
        Map<String, Map<String, Integer>> projectTeamMap = dataMap.get(maxReleaseDate);

        // Format: Project, Team Name
//        const data = [
//                { name: 'A-欧普照明', 'APP': 18.9, '财务线': 28.8, '导购线': 39.3, 'Apr.': 81.4, 'May': 47, 'Jun.': 20.3, 'Jul.': 24, 'Aug.': 35.6 },
//                { name: 'A-宜和', 'APP': 12.4, '财务线.': 23.2, '导购线': 34.5, 'Apr.': 99.7, 'May': 52.6, 'Jun.': 35.5, 'Jul.': 37.4, 'Aug.': 42.4 }
//        ];

        List<Map<String, Object>> chartDataList = getCharData(projectTeamMap);
        RMSUtil.postReport(String.format("%s_%s", getName(), getSheetName("graph")), dateStr, duration, chartDataList);

        postToRms3(records);
    }

    protected List<Map<String, Object>> getCharData(Map<String, Map<String, Integer>> projectTeamMap) {
        if (projectTeamMap == null || projectTeamMap.size() <= 0) {
            return null;
        }

        List<Map<String, Object>> chartDataList = new ArrayList<Map<String, Object>>();
        for (final Map.Entry<String, Map<String, Integer>> projectTeam : projectTeamMap.entrySet()) {
            chartDataList.add(new HashMap<String, Object>() {{
                put("name", projectTeam.getKey());
                for (Entry<String, Integer> team : projectTeam.getValue().entrySet()) {
                    put(team.getKey(), team.getValue());
                }
            }});
        }
        return chartDataList;
    }

    @Override
    protected void postToRms2(List<Map<String, String>> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        // Manipulate the data: Release Date, Team Name
        Map<String, Map<String, Double>> dataMap = new HashMap<String, Map<String, Double>>();
        String maxReleaseDate = null;
        for (Map<String, String> record : records) {
            String releaseDate = record.get(TeamProcessor.dateHeader.getName());
            String team = record.get(TeamProcessor.nameHeader.getName());

            if (maxReleaseDate == null || maxReleaseDate.compareTo(releaseDate) < 0) {
                maxReleaseDate = releaseDate;
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

        if (dataMap == null || dataMap.size() <= 0 || maxReleaseDate == null || !dataMap.containsKey(maxReleaseDate)) {
            System.out.printf("Error when postToRms2: %s\r\n", records.toString());
            return;
        }
        Map<String, Double> teamMap = dataMap.get(maxReleaseDate);

        // Format: Project, Team Name
//        const data = [
//            { month: 'APP', Current: 1.0, Max: 4, Min: 2 },
//            { month: '基础架构', Current: 4.6, Max: 4, Min: 2 }
//        ];

        List<Map<String, Object>> chartDataList = new ArrayList<Map<String, Object>>();
        for (final Map.Entry<String, Double> team : teamMap.entrySet()) {
            chartDataList.add(new HashMap<String, Object>() {{
                put("team", team.getKey());
                put("Current", team.getValue());
                put("Max", TeamEnum.APP.getReleaseMax());
                put("Min", TeamEnum.APP.getReleaseMin());
            }});
        }
        RMSUtil.postReport(String.format("%s_%s", getName(), getSheetName("graph2")), dateStr, duration, chartDataList);
    }

    protected void postToRms3(List<Map<String, String>> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        // Manipulate the data: Release Date, Team Name
        Map<String, Map<String, Integer>> dataMap = new HashMap<String, Map<String, Integer>>();
        for (Map<String, String> record : records) {
            String releaseDate = record.get(HeaderProcessor.releaseDateHeader.getName());
            String team = record.get(HeaderProcessor.teamNameHeader.getName());

            // Find the project
            if (!dataMap.containsKey(releaseDate)) {
                dataMap.put(releaseDate, new HashMap<String, Integer>());
            }
            Map<String, Integer> teamMap = dataMap.get(releaseDate);
            teamMap.put(team, teamMap.containsKey(team) ? (teamMap.get(team) + 1) : 1);
        }

        if (dataMap == null || dataMap.size() <= 0) {
            System.out.printf("Error when postToRms3: %s\r\n", records.toString());
            return;
        }

        // Format: Release Date, Team Name
//        const data = [
//                { name: '2017-12-16', 'APP': 18.9, '财务线': 28.8, '导购线': 39.3, 'Apr.': 81.4, 'May': 47, 'Jun.': 20.3, 'Jul.': 24, 'Aug.': 35.6 },
//                { name: '2017-12-16', 'APP': 12.4, '财务线.': 23.2, '导购线': 34.5, 'Apr.': 99.7, 'May': 52.6, 'Jun.': 35.5, 'Jul.': 37.4, 'Aug.': 42.4 }
//        ];

        List<Map<String, Object>> chartDataList = getCharData(dataMap);
        RMSUtil.postReport(String.format("%s_%s", getName(), getSheetName("graph3")), dateStr, duration, chartDataList);
    }
}
