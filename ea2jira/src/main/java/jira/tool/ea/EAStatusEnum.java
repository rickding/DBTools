package jira.tool.ea;

import dbtools.common.utils.StrUtils;

/**
 * Created by user on 2017/9/23.
 */
public enum EAStatusEnum {
    Approved("Approved"),
    Mandatory("Mandatory"),
    Proposed("Proposed"),
    Validated("Validated"),
    Implemented("Implemented");

    private static EAStatusEnum[] mappedToStoryList = new EAStatusEnum[]{
            Implemented,
    };

    public static boolean isMappedToStory(String status) {
        if (StrUtils.isEmpty(status)) {
            return false;
        }

        for (EAStatusEnum processStatus : mappedToStoryList) {
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
