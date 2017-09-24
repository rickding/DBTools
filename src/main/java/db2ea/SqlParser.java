package db2ea;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by user on 2017/9/23.
 */
public class SqlParser {
    public static void processFile(File file, EAWriter writer) {
        if (file == null || !file.canRead() || writer == null || !writer.isOpen()) {
            return;
        }

        // http://www.cnblogs.com/lovebread/archive/2009/11/23/1609122.html
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            EAItem pack = writer.getPack();

            // DB, table, field are parent-cline relationships.
            EAItem db = null, table = null;

            // Read and parse
            String str;
            while ((str = reader.readLine()) != null) {
                if (StrUtils.isEmpty(str)) {
                    continue;
                }

                EAItem item = parse(str.trim(), null);
                if (item == null) {
                    continue;
                }

                EAStereotype type = item.getStereotype();
                if (type == null) {
                    continue;
                }

                if (type.isDB()) {
                    db = item;
                    db.setParent(pack);
                    System.out.println(db.toString());
                } else if (type.isTable()) {
                    table = item;
                    table.setParent(db);
                    System.out.println(table.toString());
                } else if (type.isField()) {
                    item.setParent(table);
                } else {
                    System.out.printf("Un-supported item: %s\n", item.toString());
                    continue;
                }

                writer.write(item);
            }

            reader.close();
            reader = null;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    reader = null;
                }
            }
        }
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
            String name = parseName(sql, DB_Splitter, DB_Index, DB_Trim, null);
            if (!StrUtils.isEmpty(name)) {
                item = new EAItem(name, EAType.Artifact, EAStereotype.DB, parent);
            }
        } else if (sqlLowercase.startsWith(Table_Flag)) {
            sql = sql.substring(sqlLowercase.indexOf(Table_Flag));
            String name = parseName(sql, Table_Splitter, Table_Index, Table_Trim, null);
            if (!StrUtils.isEmpty(name)) {
                item = new EAItem(name, EAType.Class, EAStereotype.Table, parent);
            }
        } else if (sqlLowercase.startsWith(Field_Flag)) {
            sql = sql.substring(sqlLowercase.indexOf(Field_Flag));
            String name = parseName(sql, Field_Splitter, Field_Index, Field_Trim, Field_Ignore_list);
            if (!StrUtils.isEmpty(name)) {
                item = new EAItem(name, EAType.Class, EAStereotype.Field, parent);

                // Find the comment
                sqlLowercase = sql.toLowerCase();
                if (sqlLowercase.indexOf(Comment_Flag) >= 0) {
                    sql = sql.substring(sqlLowercase.indexOf(Comment_Flag));

                    name = parseName(sql, Comment_Splitter, Comment_Index, Comment_Trim_List, null);
                    if (!StrUtils.isEmpty(name)) {
                        item.setComment(name);
                    }
                }
            }
        }

        return item;
    }

    public static String parseName(String str, String splitter, int index, String trim, String[] ignores) {
        return parseName(str, splitter, index, StrUtils.isEmpty(trim) ? null : new String[]{trim}, ignores);
    }

    public static String parseName(String str, String splitter, int index, String[] trims, String[] ignores) {
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
                if (name.startsWith(trim)) {
                    name = name.substring(trim.length());
                }

                // Trim the ending
                if (name.endsWith(trim)) {
                    name = name.substring(0, name.length() - trim.length());
                }
            }
        }

        // Check if it should be ignored
        if (!ArrayUtils.isEmpty(ignores)) {
            List<String> list = Arrays.asList(ignores);
            if (list.contains(name)) {
                name = "";
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

    public static String File_SQL_Ext = ".sql";
    public static String File_EA_Ext = ".csv";

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
    public static String[] Field_Ignore_list = {
            "create_time", "create_by", "update_time", "update_by", "server_ip",
            "is_available", "is_deleted", "is_disable",
            "version", "version_no", "client_versionno", "company_id",
            "create_userid", "create_username", "create_userip", "create_usermac", "create_time_db",
            "update_userid", "update_username", "update_userip", "update_usermac", "update_time_db",
            "del_flg", "crt_id", "crt_time", "upd_id", "upd_time"
    };

    public static String Comment_Flag = "comment";
    public static String Comment_Splitter = " ";
    public static int Comment_Index = 1;
    public static String[] Comment_Trim_List = {",", "'", "\""};
}
