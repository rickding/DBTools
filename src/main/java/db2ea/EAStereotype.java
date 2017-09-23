package db2ea;

/**
 * Created by user on 2017/9/23.
 */
public enum EAStereotype {
    None(0, ""),
    DB(1, "database connection"),
    Table(2, "table"),
    Field(3, "function");

    private int id;
    private String code;

    private EAStereotype(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public boolean isDB() {
        return id == DB.id;
    }

    public boolean isTable() {
        return id == Table.id;
    }

    public boolean isField() {
        return id == Field.id;
    }
}
