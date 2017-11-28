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
    Wuxianfeng("PD", "wuxianfeng", new String[]{"吴先锋", "wxf"}),
    Guipeng("PD", "guipeng", new String[]{"桂鹏"}),
    Rentao("PD", "rentao", new String[]{"任韬"}),
    Liuxuanyuan("PD", "liuxuanyuan", new String[]{"柳宣渊"}),

    Mengwei("PD", "mengwei", new String[]{"孟威"}),
    Xuechao("PD", "xuechao", new String[]{"薛超"}),
    Wangxinyuan("PD", "wangxinyuan", new String[]{"王心苑"}),
    Zhanpengfei("PD", "zhanpengfei", new String[]{"詹鹏飞"}),
    Wenchangxuan("PD", "wenchangxuan", new String[]{"温昌烜"}),

    Wangshanliang("Mgr", "wangshanliang", new String[]{"王善良"}),
    Wanganqing("Mgr", "wanganqing", new String[]{"王安庆"}),
    Caoshusheng("APP", "caoshusheng", new String[]{"曹树胜"}),

    Tanbin("AA", "tanbin", new String[]{"谭镔", "谭彬"}),
    Shengjinmiao("YUYAN", "shengjinmiao", new String[]{"盛金苗"}),
    Chenying("WMS", "chenying", new String[]{"陈颖", "chenyin", "chneyin", "chenyiin"}),

    Wangdongfang("DDX", "wangdongfang", new String[]{"王东方"}),
    Churongcheng("DDX", "churongcheng", new String[]{"储荣成"}),
    Chenliangtian("DDX", "chenliangtian", new String[]{"陈良田"}),

    Zengfenghua("BI", "zengfenghua", new String[]{"曾风华"}),
    Yeyuqiang("BI", "yeyuqiang", new String[]{"叶雨强"}),
    Zhufeng("BI", "zhufeng", new String[]{"朱锋", "朱峰"}),
    Liutong("BI", "liutong", new String[]{"刘通"}),
    Wangchen("BI", "wangchen", new String[]{"王宸"}),
    Fanjinxiang("BI", "fangjinxiang", new String[]{"范金祥"}),

    Renqiuming("TRADE", "renqiuming", new String[]{"任秋明"}),
    Kangyufeng("TRADE", "kangyufeng", new String[]{"康玉峰"}),
    Fuyifan("TRADE", "fuyifan", new String[]{"傅艺帆"}),
    Shiyulong("TRADE", "shiyulong", new String[]{"石玉龙"}),

    Huangkemin("GUIDE", "huangkemin", new String[]{"黄克敏"}),
    Chenkaiqiang("GUIDE", "chenkaiqiang", new String[]{"陈开强"}),
    Xiongwenqiang("GUIDE", "xiongwenqiang", new String[]{"熊文强"}),
    Zhaowei("GUIDE", "zhaowei", new String[]{"赵伟"}),
    Hezhifeng("GUIDE", "hezhifeng", new String[]{"贺智峰", "鹤智峰"}),
    Rongjianlong("GUIDE", "rongjianlong", new String[]{"戎剑龙"}),
    Lixiaofei("GUIDE", "lixiaofei", new String[]{"李晓菲"}),

    Gexingyuan("CWX", "gexingyuan", new String[]{"葛兴元"}),
    Jiangwenqi("SPX", "jiangwenqi", new String[]{"江文奇"}),
    Chencheng("SPX", "chencheng", new String[]{"陈诚", "chencheng2"}),
    Dengxiaojie("SPX", "dengxiaojie", new String[]{"邓晓杰"}),
    Wangxiaolei("SPX", "wangxiaolei", new String[]{"王晓磊"}),

    Luoyong("SJX", "luoyong", new String[]{"罗勇"}),
    Wudi("SJX", "wudi", new String[]{"吴迪"}),
    Heshunhua("SJX", "heshunhua", new String[]{"何顺华"}),
    Chenshoujiang("SJX", "chenshoujiang", new String[]{"陈寿江"}),
    Zengqing("SJX", "zengqing", new String[]{"曾庆", "zenqing"});

    private static JiraUserEnum[] userArray = new JiraUserEnum[] {
            Luting, Caohong, Wangzhen, Zhangqie, Zhangtan, Qixiaosen, Wuxianfeng, Guipeng, Rentao, Liuxuanyuan,
            Mengwei, Xuechao, Wangxinyuan, Zhanpengfei, Wenchangxuan,
            Wangshanliang, Wanganqing, Caoshusheng,
            Tanbin, Shengjinmiao, Chenying,
            Wangdongfang, Churongcheng, Chenliangtian,
            Zengfenghua, Yeyuqiang, Zhufeng, Liutong, Wangchen, Fanjinxiang,
            Renqiuming, Kangyufeng, Fuyifan, Shiyulong,
            Huangkemin, Chenkaiqiang, Xiongwenqiang, Zhaowei, Hezhifeng, Rongjianlong, Lixiaofei,
            Gexingyuan, Jiangwenqi, Chencheng, Dengxiaojie, Wangxiaolei,
            Luoyong, Wudi, Heshunhua, Chenshoujiang, Zengqing,
    };

    public static JiraUser toUser(JiraUserEnum user) {
        if (user == null) {
            return null;
        }
        return new JiraUser(user.getTeam(), user.getName(), user.getAliases());
    }

    public static JiraUserEnum findUser(String name) {
        if (StrUtils.isEmpty(name) || userArray == null || userArray.length <= 0) {
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
