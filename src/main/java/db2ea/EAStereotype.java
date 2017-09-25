package db2ea;

/**
 * Created by user on 2017/9/23.
 */
public enum EAStereotype {
    None(0, "", ""),
    DB(1, "database connection", "数据库"),
    Table(2, "table", "数据表"),
    Field(3, "function", "字段");

    private int id;
    private String code;
    private String codeForExcel;

    EAStereotype(int id, String code, String codeForExcel) {
        this.id = id;
        this.code = code;
        this.codeForExcel = codeForExcel;
    }

    public String getCode() {
        return code;
    }

    public String getCodeForExcel() {
        return codeForExcel;
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
