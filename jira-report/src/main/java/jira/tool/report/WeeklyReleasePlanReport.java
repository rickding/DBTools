package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.DoubleUtil;
import jira.tool.db.DBUtil;
import jira.tool.db.model.Story;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.SprintDateProcessor;
import jira.tool.report.processor.TeamEnum;
import jira.tool.report.processor.TeamProcessor;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WeeklyReleasePlanReport extends ReleasePlanReport {
    public WeeklyReleasePlanReport() {
        mapSheetName.put("data", "计划交付");
        mapSheetName.put("graph", "客户统计");
        mapSheetName.put("data2", "人力库存");
        mapSheetName.put("graph2", "人天统计");

        duration = "weekly";
        fillWholeDate = true;
    }

    @Override
    public String getTemplateName() {
        return useTemplate ? "template-计划交付.xlsx" : null;
    }

    @Override
    public String getFileName() {
        return String.format("计划交付-%s.xlsx", DateUtils.format(new Date(), "MMdd"));
    }

    @Override
    protected List<Story> getStoryList() {
        return DBUtil.getReleasePlanStoryList();
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
        pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.projectHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, HeaderProcessor.headerList.indexOf(HeaderProcessor.issueKeyHeader));

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
    protected void postToRms(List<Map<String, String>> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        // Manipulate the data: Project
        final Map<String, Integer> dataMap = new HashMap<String, Integer>();
        for (Map<String, String> record : records) {
            String project = record.get(HeaderProcessor.projectHeader.getName());
            dataMap.put(project, dataMap.containsKey(project) ? (dataMap.get(project) + 1) : 1);
        }

        if (dataMap == null || dataMap.size() <= 0 ) {
            System.out.printf("Error when postToRms: %s\r\n", records.toString());
            return;
        }

        // Format: Project, Team Name
//        const data = [
//                { 'APP': 18.9, '财务线': 28.8, '导购线': 39.3, 'Apr.': 81.4, 'May': 47, 'Jun.': 20.3, 'Jul.': 24, 'Aug.': 35.6 },
//        ];

        List<Map<String, Object>> chartDataList = new ArrayList<Map<String, Object>>();
        chartDataList.add(new HashMap<String, Object>() {{
            put("name", "未来28天到期交付");
            for (final Map.Entry<String, Integer> team : dataMap.entrySet()) {
                String name = team.getKey();
                if (name.startsWith("A-WMS 2.0")) {
                    name = "A-WMS2";
                }
                put(name, team.getValue());
            }
        }});
        RMSUtil.postReport(String.format("%s_%s", getName(), getSheetName("graph")), dateStr, duration, chartDataList);
    }

    @Override
    protected void postToRms2(List<Map<String, String>> records) {
        if (records == null || records.size() <= 0) {
            return;
        }

        // Manipulate the data: Release Date, Team Name
        final Map<String, Double> timeMap = new HashMap<String, Double>();
        final Map<String, Integer> manDayMap = new HashMap<String, Integer>();

        for (Map<String, String> record : records) {
            String team = record.get(TeamProcessor.nameHeader.getName());

            // Time
            String value = record.get(TeamProcessor.timeHeader.getName());
            Double dbl = Double.valueOf(value);
            if (timeMap.containsKey(team)) {
                dbl += timeMap.get(team);
            }
            timeMap.put(team, dbl);

            // Manday
            value = record.get(TeamProcessor.manDayHeader.getName());
            int count = Integer.valueOf(value);
            if (manDayMap.containsKey(team)) {
                count += manDayMap.get(team);
            }
            manDayMap.put(team, count);
        }

        // Format: Estimation/Man-day, Team Name
//        const data = [
//                { name: 'Story估时人天', 'APP': 18.9, '财务线': 28.8, '导购线': 39.3, 'Apr.': 81.4, 'May': 47, 'Jun.': 20.3, 'Jul.': 24, 'Aug.': 35.6 },
//                { name: '库存人天', 'APP': 12.4, '财务线.': 23.2, '导购线': 34.5, 'Apr.': 99.7, 'May': 52.6, 'Jun.': 35.5, 'Jul.': 37.4, 'Aug.': 42.4 }
//        ];

        List<Map<String, Object>> chartDataList = new ArrayList<Map<String, Object>>();
        chartDataList.add(new HashMap<String, Object>() {{
            put("name", "Story估时人天");
            for (final Map.Entry<String, Double> team : timeMap.entrySet()) {
                put(team.getKey(), DoubleUtil.format(team.getValue(), 3));
            }
        }});
        chartDataList.add(new HashMap<String, Object>() {{
            put("name", "库存人天");
            for (final Map.Entry<String, Integer> team : manDayMap.entrySet()) {
                put(team.getKey(), team.getValue());
            }
        }});
        RMSUtil.postReport(String.format("%s_%s", getName(), getSheetName("graph2")), dateStr, duration, chartDataList);
    }
}
