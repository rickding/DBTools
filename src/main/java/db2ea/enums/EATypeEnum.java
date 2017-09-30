package db2ea.enums;

/**
 * Created by user on 2017/9/23.
 */
public enum EATypeEnum {
    Package("Package"),
    Artifact("Artifact"),
    Class("Class");

    private String code;

    EATypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
