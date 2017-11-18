package jira.tool.report.processor;

import dbtools.common.utils.DateUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.HashMap;
import java.util.Map;

public class TeamProcessor {
    public static HeaderProcessor dateHeader = new HeaderProcessor("到期日", "date");
    public static HeaderProcessor nameHeader = new HeaderProcessor("产品线", "name");
    public static HeaderProcessor storyHeader = new HeaderProcessor("Story数量", "story");
    public static HeaderProcessor timeHeader = new HeaderProcessor("Story估时（人天）", "estimation");
    public static HeaderProcessor releaseMaxHeader = new HeaderProcessor("交付/人天警戒值上限", "releaseMax");
    public static HeaderProcessor releaseHeader = new HeaderProcessor("交付/人天", "release");
    public static HeaderProcessor releaseMinHeader = new HeaderProcessor("交付/人天警戒值下限", "releaseMin");
    public static HeaderProcessor manDayHeader = new HeaderProcessor("剩余人天", "manDay");
    public static HeaderProcessor dayHeader = new HeaderProcessor("剩余天数", "day");
    public static HeaderProcessor memberHeader = new HeaderProcessor("人数", "member");

    private static final HeaderProcessor[] headers = {
            dateHeader, nameHeader, storyHeader, timeHeader, releaseMaxHeader, releaseHeader, releaseMinHeader, manDayHeader, dayHeader, memberHeader
    };

    /**
     * Return the headers
     * @return
     */
    public static HeaderProcessor[] getHeaders() {
        return headers;
    }

    /**
     * Create the team processors
     * @return
     */
    public static Map<String, TeamProcessor> createTeamProcessors() {
        TeamEnum[] teams = TeamEnum.getList();
        if (teams == null || teams.length <= 0) {
            return null;
        }

        Map<String, TeamProcessor> teamProcessors = new HashMap<String, TeamProcessor>();
        for (TeamEnum team : teams) {
            if (!TeamEnum.AA.getName().equalsIgnoreCase(team.getName())) { // Skip AA
                teamProcessors.put(team.getName(), new TeamProcessor(team));
            }
        }
        return teamProcessors;
    }

    public static int getWorkDays(TeamProcessor[] teams) {
        if (null == teams || teams.length < 0) {
            return 0;
        }

        int maxCount = Integer.MIN_VALUE;
        for (TeamProcessor team : teams) {
            int tmp = team.getWorkDays();
            if (maxCount < tmp) {
                maxCount = tmp;
            }
        }
        return maxCount;
    }

    private TeamEnum team;
    private Map<String, Integer> dateStoryMap = new HashMap<String, Integer>();
    private Map<String, Double> dateTimeMap = new HashMap<String, Double>();

    public TeamProcessor(TeamEnum team) {
        this.team = team;
    }

    /**
     * Count the story
     * @param date
     */
    public void countStory(String date, double time) {
        Integer count = null;
        if (!dateStoryMap.containsKey(date) || dateStoryMap.get(date) == null) {
            count = new Integer(1);
        } else {
            count = dateStoryMap.get(date) + 1;
        }
        dateStoryMap.put(date, count);

        Double totalTime = null;
        if (!dateTimeMap.containsKey(date) || dateTimeMap.get(date) == null) {
            totalTime = new Double(0.0);
        } else {
            totalTime = dateTimeMap.get(date) + time;
        }
        dateTimeMap.put(date, totalTime);
    }

    public int getWorkDays() {
        if (dateStoryMap == null || dateStoryMap.size() <= 0) {
            return 0;
        }

        int count = 0;
        String today = DateUtils.format(SprintDateProcessor.today, "yyyy-MM-dd");
        for (Map.Entry<String, Integer> dateStory : dateStoryMap.entrySet()) {
            String date = dateStory.getKey();
            count += SprintDateProcessor.getLeftWorkDays(date, today);
        }
        return count;
    }

    /**
     * Fill the data
     * @param sheet
     * @param row
     * @return
     */
    public int fillRow(XSSFSheet sheet, int row) {
        if (sheet == null || row < 0 || team == null || dateStoryMap == null || dateStoryMap.size() <= 0) {
            return 0;
        }

        String today = DateUtils.format(SprintDateProcessor.today, "yyyy-MM-dd");

        // Walk through the data
        int newRow = row;
        for (Map.Entry<String, Integer> dateStory : dateStoryMap.entrySet()) {
            String date = dateStory.getKey();
            Integer story = dateStory.getValue();
            if (story == null) {
                story = 0;
            }

            // Check the date
            int day = SprintDateProcessor.getLeftWorkDays(date, today);
            int manDay = team.getMember() * day;

            // Write data
            Row r = sheet.createRow(newRow++);
            int col = 0;
            r.createCell(col++).setCellValue(date);
            r.createCell(col++).setCellValue(team.getName());
            r.createCell(col++).setCellValue(story);
            r.createCell(col++).setCellValue(dateTimeMap.get(date));
            r.createCell(col++).setCellValue(team.getReleaseMax());
            r.createCell(col++).setCellValue(manDay == 0 ? 0.0 : (double) story / manDay);
            r.createCell(col++).setCellValue(team.getReleaseMin());
            r.createCell(col++).setCellValue(manDay);
            r.createCell(col++).setCellValue(day);
            r.createCell(col++).setCellValue(team.getMember());
        }

        return newRow - row;
    }
}
