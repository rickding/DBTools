package jira.tool.db;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum JiraQAEnum {
    zhuruiying("朱瑞莹", "zhuruiying", new String[]{"商品线", "SPX"}),
    yangfuyi("杨馥伊", "yangfuyi", new String[]{"订单线", "DDX"}),
    huwenting("胡文婷", "huwenting", new String[]{"导购线", "GUIDE", "DGX"}),
    licunying("李存英", "licunying", new String[]{"交易线", "TRADE", "JYX"}),
    zhaoliang("赵亮", "zhaoliang", new String[]{"供应链", "WMS", "STB"}),
    zhangjingjing("张静静", "zhangjingjing", new String[]{"财务线", "分销线", "CWX", "FNX", "BI", "大数据", "智能平台"}),
    liufuming("刘福明", "liufuming", new String[]{"商家线", "用户线", "SJX", "YHX"}),
    guoguilin("郭桂林", "guoguilin", new String[]{"APP", "IOS", "APPA"});

    private static JiraQAEnum[] userArray = new JiraQAEnum[] {
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

    private String name;
    private String code;
    private List<String> teams = new ArrayList<String>();

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
}
