package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum JiraUserEnum {
    Luting("PD", "luting", new String[]{"陆霆"}),
    Caohong("PD", "caohong", new String[]{"曹红"}),
    Wangzhen("PD", "wangzhen", new String[]{"王桢"}),
    Zhangqie("PD", "zhangqie", new String[]{"张慊"}),
    Zhangtan("PD", "zhangtan", new String[]{"张郯"}),
    Qixiaosen("PD", "qixiaosen", new String[]{"齐小森"}),

    Zengfenghua("BI", "zengfenghua", new String[]{"曾风华"}),
    Kangyufeng("Trade", "kangyufeng", new String[]{"康玉峰"}),

    Chenkaiqiang("Guide", "chenkaiqiang", new String[]{"陈开强"}),
    Xiongwenqiang("Guide", "xiongwenqiang", new String[]{"熊文强"}),
    Zhaowei("Guide", "zhaowei", new String[]{"赵伟"}),
    Hezhifeng("Guide", "hezhifeng", new String[]{"贺智峰"}),
    Rongjianlong("Guide", "rongjianlong", new String[]{"戎剑龙"}),

    Gexingyuan("CWX", "gexingyuan", new String[]{"葛兴元"}),
    Jiangwenqi("SPX", "jiangwenqi", new String[]{"江文奇"});

    private static JiraUserEnum[] userArray = new JiraUserEnum[] {
            Luting, Caohong, Wangzhen, Zhangqie, Zhangtan, Qixiaosen,
            Zengfenghua, Kangyufeng,
            Chenkaiqiang, Xiongwenqiang, Zhaowei, Hezhifeng, Rongjianlong,
            Gexingyuan, Jiangwenqi,
    };

    public static JiraUserEnum findUser(String name) {
        if (StrUtils.isEmpty(name)) {
            return null;
        }

        name = name.trim();

        for (JiraUserEnum user : userArray) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }

            for (String alias : user.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return user;
                }
            }
        }

        return null;
    }

    private String team;
    private String name;
    private List<String> aliases = new ArrayList<String>();

    JiraUserEnum(String team, String name, String[] aliases) {
        this.team = team;
        this.name = name;

        if (!ArrayUtils.isEmpty(aliases)) {
            this.aliases.addAll(Arrays.asList(aliases));
        }
    }

    public String getTeam() {
        return team;
    }

    public String getName() {
        return name;
    }

    public List<String> getAliases() {
        return aliases;
    }
}
