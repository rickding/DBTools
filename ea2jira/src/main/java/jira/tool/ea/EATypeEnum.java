package jira.tool.ea;

import dbtools.common.utils.StrUtils;

/**
 * Created by user on 2017/9/23.
 */
public enum EATypeEnum {
    Requirement("Requirement");

    private static EATypeEnum[] mappedToStoryList = new EATypeEnum[]{
            EATypeEnum.Requirement
    };

    public static boolean isMappedToStory(String type) {
        if (StrUtils.isEmpty(type)) {
            return false;
        }

        for (EATypeEnum processType : mappedToStoryList) {
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
