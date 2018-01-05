package dbtools.sql.generator;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.util.List;

public class SqlGenerator {
    private static String useDB = "use jiradb;";
    private static String teamName = "导购线";
    private static String teamFlag = "set @teamFlag = '导购线';";
    private static String projectName = "A-欧普照明";
    private static String projectFlag = String.format("set @projectFlag = '%s';", projectName);
    private static String weeklyHoursName = "40";
    private static String weeklyHoursFlag = "set @weeklyHours = 40;";

    public static void process(List<String> sqlList, String[] templates, String team, String project, int weeklyHours) {
        if (sqlList == null || ArrayUtils.isEmpty(templates) || StrUtils.isEmpty(team) || StrUtils.isEmpty(project)) {
            return;
        }

        if (weeklyHours < 0) {
            weeklyHours = 40;
        }

        for (String template : templates) {
            if (useDB.equalsIgnoreCase(template)) {
                if (sqlList.size() > 0) {
                    continue;
                }
            } else if (teamFlag.equalsIgnoreCase(template)) {
                template = template.replace(teamName, team);
            } else if (projectFlag.equalsIgnoreCase(template)) {
                template = template.replace(projectName, project);
            } else if (weeklyHoursFlag.equalsIgnoreCase(template)) {
                template = template.replace(weeklyHoursName, String.valueOf(weeklyHours));
            }
            sqlList.add(template);
        }
    }
}
