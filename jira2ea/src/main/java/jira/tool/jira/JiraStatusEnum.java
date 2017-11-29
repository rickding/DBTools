package jira.tool.jira;

import dbtools.common.utils.StrUtils;

public enum JiraStatusEnum {
    Pending("PENDING"),
    Closed("关闭");

    public static boolean isClosed(String status) {
        if (StrUtils.isEmpty(status)) {
            return false;
        }

        status = status.trim();
        for (JiraStatusEnum processStatus : new JiraStatusEnum[] {Closed, Pending}) {
            if (processStatus.code.equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    private String code;

    JiraStatusEnum(String code) {
        this.code = code;
    }
}
