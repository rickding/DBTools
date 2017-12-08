package jira.tool.report.processor;

import jira.tool.db.JiraUtil;
import jira.tool.db.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum TeamEnum {
    APP("APP", "", 7),
    CW("财务线", "", 5),
    DG("导购线", "", 14),
    DD("订单线", "", 7),
    GYL("供应链", "", 14),
    BA("基础架构", "", 5),
    JY("交易线", "", 8),
    SJ("商家线", "", 10),
    SP("商品线", "", 8),
    BI("智能平台", "", 7),

    SAAS("SaaS", "", 0),
    AA("应用架构", "", 0);

    private static final TeamEnum[] list = {
            APP, CW, DG, DD, GYL, BA, JY, SJ, SP, BI, SAAS, AA
    };

    public static TeamEnum[] getList() {
        return list;
    }

    public static int getTotalMember() {
        checkTeamMemberCount();

        int count = 0;
        for (TeamEnum team : getList()) {
            count += team.member;
        }
        return count;
    }

    public static void checkTeamMemberCount() {
        List<User> teams = JiraUtil.getTeamMembersCountList();
        if (teams == null || teams.size() <= 0) {
            return;
        }

        Map<String, Integer> teamCountMap = new HashMap<String, Integer>();
        for (User team : teams) {
            teamCountMap.put(team.getTeam(), team.getMemberCount());
        }

        for (TeamEnum team : getList()) {
            String name = team.name;
            int count = teamCountMap.containsKey(name) ? teamCountMap.get(name) : 0;
            if (team.member != count) {
                System.out.printf("%s, member count in code: %d,  from db: %d\r\n", team.name, team.member, count);
                if (team.member > 0) {
                    team.member = count;
                }
            }
        }
    }

    private String name;
    private String key;
    private int member;
    private double releaseMin = 0.2;
    private double releaseMax = 0.4;

    TeamEnum(String name, String key, int member) {
        this.name = name;
        this.key = key;
        this.member = member;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return key;
    }

    public int getMember() {
        return member;
    }

    public double getReleaseMin() {
        return releaseMin;
    }

    public double getReleaseMax() {
        return releaseMax;
    }
}
