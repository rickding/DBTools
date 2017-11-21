package jira.tool.ea;

import dbtools.common.utils.StrUtils;

/**
 * Created by user on 2017/9/23.
 */
public enum EAStatusEnum {
    Implemented("Implemented");

    private static EAStatusEnum[] processedStatus = new EAStatusEnum[]{
            Implemented,
    };

    public static boolean isProcessStatus(String status) {
        if (StrUtils.isEmpty(status)) {
            return false;
        }

        for (EAStatusEnum processStatus : processedStatus) {
            if (processStatus.getCode().equalsIgnoreCase(status)) {
                return true;
            }
        }
        return false;
    }

    private String code;

    EAStatusEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
