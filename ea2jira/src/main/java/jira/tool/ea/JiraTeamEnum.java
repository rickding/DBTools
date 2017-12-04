package jira.tool.ea;

import dbtools.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.List;

public enum JiraTeamEnum {
    APP("APP"),
    iOS("IOS"),
    Android("APPA");

    public static boolean isStoryInIgnoredTeam(String key) {
        // If the element has two same stories in duplicated teams, ignore it
        if (StrUtils.isEmpty(key)) {
            return false;
        }

        key = key.trim().toUpperCase();
        return key.startsWith(Android.code);
    }


    public static String[] getTeams(String team) {
        if (StrUtils.isEmpty(team)) {
            return null;
        }

        List<String> teams = new ArrayList<String>(2);
        if (JiraTeamEnum.APP.getCode().equalsIgnoreCase(team)) {
            teams.add(iOS.code);
            teams.add(Android.code);
        } else {
            teams.add(team);
        }

        String[] tmpArr = new String[teams.size()];
        teams.toArray(tmpArr);
        return tmpArr;
    }

    private String code;

    JiraTeamEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
