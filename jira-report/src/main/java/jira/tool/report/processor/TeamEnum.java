package jira.tool.report.processor;

public enum TeamEnum {
    APP("APP", "", 9),
    CW("财务线", "", 5),
    DG("导购线", "", 14),
    DD("订单线", "", 7),
    GYL("供应链", "", 14),
    BA("基础架构", "", 5),
    JY("交易线", "", 8),
    SJ("商家线", "", 10),
    SP("商品线", "", 8),
    AA("应用架构", "", 1),
    BI("智能平台", "", 7);

    private static final TeamEnum[] list = {
            APP, CW, DG, DD, GYL, BA, JY, SJ, SP, AA, BI
    };

    public static TeamEnum[] getList() {
        return list;
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
