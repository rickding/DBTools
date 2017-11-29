package jira.tool.ea;

import dbtools.common.utils.StrUtils;

/**
 * Created by user on 2017/9/23.
 */
public enum EATypeEnum {
    Package("Package"),
    Requirement("Requirement");

    private static EATypeEnum[] mappedToStoryList = new EATypeEnum[]{
            Requirement
    };

    public static boolean isMappedToStory(String type) {
        return containsType(type, mappedToStoryList);
    }

    private static EATypeEnum[] savedTypeList = new EATypeEnum[] {
            Package, Requirement,
    };

    public static boolean isSavedType(String type) {
        return containsType(type, savedTypeList);
    }

    public static boolean containsType(String type, EATypeEnum[] typeList) {
        if (StrUtils.isEmpty(type) || typeList == null || typeList.length <= 0) {
            return false;
        }

        for (EATypeEnum processType : typeList) {
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
