package jira.tool.jira;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

public enum JiraStatusEnum {
    Pending("PENDING"),
    Closed("关闭", new String[]{});

    public static boolean isClosed(String status) {
        if (StrUtils.isEmpty(status)) {
            return false;
        }

        status = status.trim();
        for (JiraStatusEnum processStatus : new JiraStatusEnum[] {Closed, Pending}) {
            if (processStatus.code.equalsIgnoreCase(status)) {
                return true;
            }

            if (!ArrayUtils.isEmpty(processStatus.aliases)) {
                for (String alias : processStatus.aliases) {
                    if (alias.equalsIgnoreCase(status)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String code;
    private String[] aliases;

    JiraStatusEnum(String code) {
        this.code = code;
    }

    JiraStatusEnum(String code, String[] aliases) {
        this.code = code;
        this.aliases = aliases;
    }
}
