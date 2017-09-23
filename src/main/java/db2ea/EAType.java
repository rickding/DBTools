package db2ea;

/**
 * Created by user on 2017/9/23.
 */
public enum EAType {
    Artifact("Artifact"),
    Class("Class");

    private String code;

    private EAType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
