package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

public enum JiraProjectEnum {
    DeSheng("A-德升", new String[]{"德升"}),
    HaiHangERP("A-海航", new String[]{"海航ERP"}),
    HaiHangOO("A-海航", new String[]{"海航O+O"}),
    HaiHangB2B("A-海航", new String[]{"海航B2B"}),
    JingKeLong("A-京客隆", new String[]{"京客隆"}),
    OuPu("A-欧普照明", new String[]{"欧普"}),
    PingAn("A-平安租赁", new String[]{"平安"}),
    ShiTaiBo("A-史泰博", new String[]{"史泰博"}),
    LaiYiFen("A-来伊份", new String[]{"来伊份"}),

    YunDianPu("A-海航", new String[]{"云店铺"}),
    BI("D-技术优化", new String[]{"大数据", "技术-大数据"}),
    DGX("D-技术优化", new String[]{"导购线", "技术-导购线"}),
    JYX("D-技术优化", new String[]{"交易线", "技术-交易线"}),
    SJX("D-技术优化", new String[]{"商家线", "技术-商家线", "用户线", "技术-用户线"}),
    SPX("D-技术优化", new String[]{"商品线", "技术-商品线"}),

    TechDebt("D-技术优化", new String[]{
            "技术优化", "大数据", "导购线", "交易线", "商家线", "商品线", "用户线",
            "技术", "one instance改造方案"
    });

    private static JiraProjectEnum[] projectArray = new JiraProjectEnum[]{
            DeSheng, HaiHangERP, HaiHangOO, HaiHangB2B, JingKeLong, OuPu, PingAn, ShiTaiBo, LaiYiFen,
            YunDianPu, BI, DGX, JYX, SJX, SPX,
            TechDebt,
    };

    public static JiraProjectEnum findProject(String fileName) {
        if (StrUtils.isEmpty(fileName) || projectArray == null || projectArray.length <= 0) {
            return null;
        }

        String name = fileName.toLowerCase();
        for (JiraProjectEnum project : projectArray) {
            if (name.startsWith(project.getName().toLowerCase())) {
                return project;
            }

            String[] prefixes = project.getPrefixes();
            if (ArrayUtils.isEmpty(prefixes)) {
                continue;
            }

            for (String prefix : prefixes) {
                if (name.startsWith(prefix.toLowerCase())) {
                    return project;
                }
            }
        }

        return null;
    }

    private String name;
    private String[] prefixes;

    JiraProjectEnum(String name, String[] prefixes) {
        this.name = name;
        this.prefixes = prefixes;
    }

    public String getName() {
        return name;
    }

    public String[] getPrefixes() {
        return prefixes;
    }
}
