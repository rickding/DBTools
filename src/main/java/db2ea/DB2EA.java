package db2ea;

/**
 * Hello world!
 */
public class DB2EA {
    public static void main(String[] args) {
        System.out.println("Hello DB2EA!");
    }

    public static EAItem parse(String sql, EAItem parent) {
        if (StrUtils.isEmpty(sql)) {
            return null;
        }

        EAItem item = null;
        String sqlLowercase = sql.toLowerCase();

        // Check DB, table, field and comment. Note: DB and comment are not at the beginning, while table and field are the first ones.
        if (sqlLowercase.indexOf(DB_Flag) >= 0) {
            // Remove the unused beginning.
            sql = sql.substring(sqlLowercase.indexOf(DB_Flag));
            String name = parseName(sql, DB_Splitter, DB_Index, DB_Trim);
            if (!StrUtils.isEmpty(name)) {
                item = new EAItem(name, EAType.Artifact, EAStereotype.DB, parent);
            }
        } else if (sqlLowercase.indexOf(Table_Flag) == 0) {
            sql = sql.substring(sqlLowercase.indexOf(Table_Flag));
            String name = parseName(sql, Table_Splitter, Table_Index, Table_Trim);
            if (!StrUtils.isEmpty(name)) {
                item = new EAItem(name, EAType.Class, EAStereotype.Table, parent);
            }
        } else if (sqlLowercase.indexOf(Field_Flag) == 0) {
            sql = sql.substring(sqlLowercase.indexOf(Field_Flag));
            String name = parseName(sql, Field_Splitter, Field_Index, Field_Trim);
            if (!StrUtils.isEmpty(name)) {
                item = new EAItem(name, EAType.Class, EAStereotype.Field, parent);

                // Find the comment
                sqlLowercase = sql.toLowerCase();
                if (sqlLowercase.indexOf(Comment_Flag) >= 0) {
                    sql = sql.substring(sqlLowercase.indexOf(Comment_Flag));

                    name = parseName(sql, Comment_Splitter, Comment_Index, Comment_Trim_List);
                    if (!StrUtils.isEmpty(name)) {
                        item.setComment(name);
                    }
                }
            }
        }

        return item;
    }

    public static String parseName(String str, String splitter, int index, String trim) {
        return parseName(str, splitter, index, StrUtils.isEmpty(trim) ? null : new String[] {trim});
    }

    public static String parseName(String str, String splitter, int index, String[] trims) {
        if (StrUtils.isEmpty(str) || StrUtils.isEmpty(splitter) || index < 0) {
            return str == null ? "" : str;
        }

        // Process with only lowercase
        String strLowercase = str.toLowerCase();

        // Split
        String[] values = strLowercase.split(splitter);
        if (index >= values.length || StrUtils.isEmpty(values[index])) {
            return "";
        }

        // Get the value
        String name = values[index];
        name = name.trim();

        // Trim
        if (!ArrayUtils.isEmpty(trims)) {
            for (String trim : trims) {
                // Trim the beginning
                if (name.indexOf(trim) == 0) {
                    name = name.substring(trim.length());
                }

                // Trim the ending
                if (name.lastIndexOf(trim) == name.length() - trim.length()) {
                    name = name.substring(0, name.length() - trim.length());
                }
            }
        }

        // Get the original string
        if (!StrUtils.isEmpty(name)) {
            index = strLowercase.indexOf(name);
            if (index >= 0) {
                name = str.substring(index, index + name.length());
            }
        }
        return name;
    }

    // DB, Table, Field (Comment).
    public static String DB_Flag = "database";
    public static String DB_Splitter = ":";
    public static int DB_Index = 1;
    public static String DB_Trim = " ";

    public static String Table_Flag = "create table";
    public static String Table_Splitter = " ";
    public static int Table_Index = 2;
    public static String Table_Trim = "`";

    public static String Field_Flag = "`";
    public static String Field_Splitter = " ";
    public static int Field_Index = 0;
    public static String Field_Trim = "`";

    public static String Comment_Flag = "comment";
    public static String Comment_Splitter = " ";
    public static int Comment_Index = 1;
    public static String[] Comment_Trim_List = {",", "'", "\""};
}
