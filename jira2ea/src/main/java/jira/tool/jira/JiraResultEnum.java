package jira.tool.jira;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.ea.EAStatusEnum;

import java.util.HashMap;
import java.util.Map;

public enum JiraResultEnum {
    Canceled("取消"),
    Finished("完成", new String[]{"Done"}),
    Resolved("已解决");

    private static Map<JiraResultEnum, EAStatusEnum> statusMap = new HashMap<JiraResultEnum, EAStatusEnum>() {{
        put(Canceled, EAStatusEnum.Approved);
        put(Finished, EAStatusEnum.Approved);
        put(Resolved, EAStatusEnum.Approved);
    }};

    public static EAStatusEnum toEAStatus(String status) {
        if (StrUtils.isEmpty(status)) {
            return null;
        }

        status = status.trim();
        for (Map.Entry<JiraResultEnum, EAStatusEnum> statusEntry : statusMap.entrySet()) {
            JiraResultEnum result = statusEntry.getKey();
            if (result.code.equalsIgnoreCase(status)) {
                return statusEntry.getValue();
            }

            if (!ArrayUtils.isEmpty(result.aliases)) {
                for (String res : result.aliases) {
                    if (res.equalsIgnoreCase(status)) {
                        return statusEntry.getValue();
                    }
                }
            }
        }
        return null;
    }

    private String code;
    private String[] aliases;

    JiraResultEnum(String code) {
        this.code = code;
    }

    JiraResultEnum(String code, String[] aliases) {
        this.code = code;
        this.aliases = aliases;
    }
}
