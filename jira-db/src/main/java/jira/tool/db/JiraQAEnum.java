package jira.tool.db;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum JiraQAEnum {
    noQA("无需测试", "无需测试", null, new String[]{"无需测试", "不需测试", "不需QA", "无需QA"}),
    wuxiancai("伍先材", "wuxiancai", null, new String[]{"武先材"}),
    panlele("潘乐乐", "panlele", null, new String[]{"潘乐乐1h"}),
    zhuruiying("朱瑞莹", "zhuruiying", new String[]{"商品线", "SPX"}),
    yangfuyi("杨馥伊", "yangfuyi", new String[]{"订单线", "DDX"}, new String[]{"杨馥依"}),
    huwenting("胡文婷", "huwenting", new String[]{"导购线", "GUIDE", "DGX"}, new String[]{"测试胡文婷"}),
    licunying("李存英", "licunying", new String[]{"交易线", "TRADE", "JYX"}),
    zhaoliang("赵亮", "zhaoliang", new String[]{"供应链", "WMS", "STB"}),
    zhangjingjing("张静静", "zhangjingjing", new String[]{"财务线", "分销线", "CWX", "FNX", "BI", "大数据", "智能平台"}),
    liufuming("刘福明", "liufuming", new String[]{"商家线", "用户线", "SJX", "YHX"}),
    guoguilin("郭桂林", "guoguilin", new String[]{"APP", "IOS", "APPA"});

    private static JiraQAEnum[] userArray = new JiraQAEnum[] {
            noQA, wuxiancai, panlele,
            zhuruiying, yangfuyi, huwenting, licunying, zhaoliang, zhangjingjing, liufuming, guoguilin,
    };

    public static JiraQAEnum findQA(String teamName) {
        if (StrUtils.isEmpty(teamName) || userArray == null || userArray.length <= 0) {
            return null;
        }

        teamName = teamName.trim();
        for (JiraQAEnum user : userArray) {
            for (String team : user.teams) {
                if (team.equalsIgnoreCase(teamName)) {
                    return user;
                }
            }
        }
        return null;
    }

    public static JiraQAEnum findQAByName(String name) {
        if (StrUtils.isEmpty(name) || userArray == null || userArray.length <= 0) {
            return null;
        }

        name = name.trim();
        for (JiraQAEnum user : userArray) {
            if (user.name.equalsIgnoreCase(name)) {
                return user;
            }
            if (user.code.equalsIgnoreCase(name)) {
                return user;
            }

            for (String alias : user.aliases) {
                if (alias.equalsIgnoreCase(name)) {
                    return user;
                }
            }
        }
        return null;
    }

    public static String QA_Team_Name = "测试部";

    public static JiraUser toUser(JiraQAEnum user) {
        if (user == null) {
            return null;
        }
        return new JiraUser(QA_Team_Name, user.name, user.code, user.getAliases());
    }

    private String name;
    private String code;
    private List<String> aliases = new ArrayList<String>();
    private List<String> teams = new ArrayList<String>();

    JiraQAEnum(String name, String code, String[] teams, String[] aliases) {
        this.name = name;
        this.code = code;

        if (!ArrayUtils.isEmpty(teams)) {
            this.teams.addAll(Arrays.asList(teams));
        }

        if (!ArrayUtils.isEmpty(aliases)) {
            this.aliases.addAll(Arrays.asList(aliases));
        }
    }

    JiraQAEnum(String name, String code, String[] teams) {
        this.name = name;
        this.code = code;

        if (!ArrayUtils.isEmpty(teams)) {
            this.teams.addAll(Arrays.asList(teams));
        }
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
