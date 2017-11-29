package jira.tool.jira;

import dbtools.common.utils.StrUtils;
import jira.tool.ea.EAStatusEnum;

import java.util.HashMap;
import java.util.Map;

public enum JiraResultEnum {
    Canceled("取消"),
    Finished("完成"),
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
            if (statusEntry.getKey().code.equalsIgnoreCase(status)) {
                return statusEntry.getValue();
            }
        }
        return null;
    }

    private String code;

    JiraResultEnum(String code) {
        this.code = code;
    }
}
