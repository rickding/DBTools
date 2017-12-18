package ea.tool.api;

/**
 * Created by user on 2017/9/23.
 */
public enum EAStatusEnum {
    Approved("Approved"),
    Released("完成"),
    Canceled("取消"),
    Mandatory("Mandatory"),
    Proposed("Proposed"),
    Validated("Validated"),
    Implemented("Implemented");

    public static boolean isUpdatedFromStory(String status) {
        return isSpecifiedStatus(status, new EAStatusEnum[]{
                Implemented,
                Approved,
        });
    }

    public static boolean isMappedToStory(String status) {
        return isSpecifiedStatus(status, new EAStatusEnum[]{
                Implemented,
        });
    }

    public static boolean isSpecifiedStatus(String status, EAStatusEnum[] statusList) {
        if (status == null || status.trim().length() <= 0 || statusList == null || statusList.length <= 0) {
            return false;
        }

        for (EAStatusEnum processStatus : statusList) {
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
