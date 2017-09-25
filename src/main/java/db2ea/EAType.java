package db2ea;

/**
 * Created by user on 2017/9/23.
 */
public enum EAType {
    Package("Package"),
    Artifact("Artifact"),
    Class("Class");

    private String code;

    EAType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
