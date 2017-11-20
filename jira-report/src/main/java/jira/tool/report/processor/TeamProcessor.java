package jira.tool.report.processor;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TeamProcessor {
    public static HeaderProcessor dateHeader = new HeaderProcessor("到期日", "date");
    public static HeaderProcessor nameHeader = new HeaderProcessor("产品线", "name");
    public static HeaderProcessor storyHeader = new HeaderProcessor("Story数量", "story");
    public static HeaderProcessor timeHeader = new HeaderProcessor("Story估时人天", "estimation");
    public static HeaderProcessor releaseMaxHeader = new HeaderProcessor("交付/人天警戒值上限", "releaseMax");
    public static HeaderProcessor releaseHeader = new HeaderProcessor("交付/人天", "release");
    public static HeaderProcessor releaseMinHeader = new HeaderProcessor("交付/人天警戒值下限", "releaseMin");
    public static HeaderProcessor manDayHeader = new HeaderProcessor("库存人天", "manDay");
    public static HeaderProcessor dayHeader = new HeaderProcessor("剩余天数", "day");
    public static HeaderProcessor memberHeader = new HeaderProcessor("人数", "member");

    private static final HeaderProcessor[] headers = {
            dateHeader, nameHeader, storyHeader, timeHeader, releaseMaxHeader, releaseHeader, releaseMinHeader, manDayHeader, dayHeader, memberHeader
    };

    /**
     * Return the headers
     *
     * @return
     */
    public static HeaderProcessor[] getHeaders() {
        return headers;
    }

    /**
     * Create the team processors
     *
     * @return
     */
    public static Map<String, TeamProcessor> createTeamProcessors() {
        TeamEnum[] teams = TeamEnum.getList();
        if (teams == null || teams.length <= 0) {
            return null;
        }

        Map<String, TeamProcessor> teamProcessors = new HashMap<String, TeamProcessor>();
        for (TeamEnum team : teams) {
            if (TeamEnum.AA.getName().equalsIgnoreCase(team.getName())) {
                continue;
            }
            teamProcessors.put(team.getName().toLowerCase(), new TeamProcessor(team));
        }
        return teamProcessors;
    }

    public static void fillWholeDate(TeamProcessor[] teams) {
        if (teams == null || teams.length < 0) {
            return;
        }

        Set<String> dateSet = new HashSet<String>();
        for (TeamProcessor team : teams) {
            dateSet.addAll(team.dateStoryMap.keySet());
        }

        if (dateSet.size() > 0) {
            for (TeamProcessor team : teams) {
                team.fillDate(dateSet);
            }
        }
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
     *
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

    public void fillDate(Set<String> dateSet) {
        if (dateSet == null || dateSet.size() <= 0) {
            return;
        }

        for (String date : dateSet) {
            if (!StrUtils.isEmpty(date)) {
                if (!dateStoryMap.containsKey(date)) {
                    dateStoryMap.put(date, new Integer(0));
                    System.out.printf("Fill date to story map: %s, %s\n", team.getName(), date);
                }
                if (!dateTimeMap.containsKey(date)) {
                    dateTimeMap.put(date, new Double(0.0));
                }
            }
        }
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
     *
     * @param sheet
     * @param row
     * @return
     */
    public int fillRow(XSSFSheet sheet, int row, boolean isPlanDate) {
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
            int day = isPlanDate ? SprintDateProcessor.getLeftWorkDays(date, today) : 5;
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
