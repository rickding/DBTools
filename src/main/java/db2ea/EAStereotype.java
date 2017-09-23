package db2ea;

/**
 * Created by user on 2017/9/23.
 */
public enum EAStereotype {
    DB("database connection"),
    Table("table"),
    Field("function");

    private String code;

    private EAStereotype(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
