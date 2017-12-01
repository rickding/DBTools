package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

public enum JiraProjectEnum {
    DeSheng("A-德升", new String[]{"德升需求管理", "德升"}),
    HaiHangB2B2C("A-海航", new String[]{"海航B2B2C需求管理", "海航B2B2C"}),
    HaiHangB2B("A-海航", new String[]{"海航B2B需求管理", "海航B2B"}),
    HaiHangERP("A-海航", new String[]{"海航ERP需求管理", "海航ERP"}),
    HaiHangOO("A-海航", new String[]{"海航O+O需求管理", "海航O+O"}),
    JingKeLong("A-京客隆", new String[]{"京客隆O+O需求管理", "京客隆O+O", "京客隆"}),
    LaiYiFen("A-来伊份", new String[]{"来伊份需求管理", "来伊份"}),
    OuPu("A-欧普照明", new String[]{"欧普需求管理", "欧普照明", "欧普"}),
    PingAn("A-平安租赁", new String[]{"平安租赁需求管理", "平安租赁", "平安"}),
    ShiTaiBo("A-史泰博", new String[]{"史泰博OMS需求管理", "史泰博OMS", "史泰博"}),

    WuRenDian("A-海航", new String[]{"无人店项目需求", "无人店需求管理", "无人店"}),
    CloudPOS("A-海航", new String[]{"云POS项目需求", "云POS需求管理", "云POS"}),
    YunDianPu("A-海航", new String[]{"云店铺项目需求", "云店铺需求管理", "云店铺"}),

    BI("D-技术优化", new String[]{"大数据", "技术优化-大数据"}),
    DGX("D-技术优化", new String[]{"导购线", "技术优化-导购线"}),
    JYX("D-技术优化", new String[]{"交易线", "技术优化-交易线"}),
    SJX("D-技术优化", new String[]{"商家线", "技术优化-商家线"}),
    SPX("D-技术优化", new String[]{"商品线", "技术优化-商品线"}),
    YHX("D-技术优化", new String[]{"用户线", "技术优化-用户线"}),

    TechDebt("D-技术优化", new String[]{
            "技术优化", "大数据", "导购线", "交易线", "商家线", "商品线", "用户线",
            "技术", "one instance改造方案"
    });

    private static JiraProjectEnum[] projectArray = new JiraProjectEnum[]{
            DeSheng, HaiHangERP, HaiHangOO, HaiHangB2B2C, HaiHangB2B, JingKeLong, OuPu, PingAn, ShiTaiBo, LaiYiFen,
            WuRenDian, CloudPOS, YunDianPu,
            BI, DGX, JYX, SJX, SPX, YHX, TechDebt,
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
