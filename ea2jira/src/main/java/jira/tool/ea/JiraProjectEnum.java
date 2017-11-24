package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

public enum JiraProjectEnum {
    DeSheng("A-德升", new String[]{"德升"}),
    HaiHang("A-海航", new String[]{"海航"}),
    JingKeLong("A-京客隆", new String[]{"京客隆"}),
    OuPu("A-欧普照明", new String[]{"欧普"}),
    PingAn("A-平安租赁", new String[]{"平安"}),
    ShiTaiBo("A-史泰博", new String[]{"史泰博"}),

    YunDianPu("A-海航", new String[]{"云店铺"}),
    TechDebt("D-技术优化", new String[]{"大数据", "交易线", "用户线", "技术-商品线", "one instance改造方案"});

    private static JiraProjectEnum[] projectArray = new JiraProjectEnum[]{
            DeSheng, HaiHang, JingKeLong, OuPu, PingAn, ShiTaiBo,
            YunDianPu,
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
