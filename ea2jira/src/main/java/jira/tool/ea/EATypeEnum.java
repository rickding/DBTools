package jira.tool.ea;

import dbtools.common.utils.StrUtils;

/**
 * Created by user on 2017/9/23.
 */
public enum EATypeEnum {
    Package("Package"),
    Requirement("Requirement");

    private static EATypeEnum[] processedTypes = new EATypeEnum[]{
            EATypeEnum.Package, EATypeEnum.Requirement
    };

    public static boolean isProcessedType(String type) {
        if (StrUtils.isEmpty(type)) {
            return false;
        }

        for (EATypeEnum processType : processedTypes) {
            if (processType.getCode().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }

    private String code;

    EATypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
