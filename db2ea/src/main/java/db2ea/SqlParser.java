package db2ea;

import db2ea.enums.EAStereotypeEnum;
import db2ea.enums.EATypeEnum;
import db2ea.utils.ArrayUtils;
import db2ea.utils.StrUtils;

import java.io.*;
import java.util.*;

/**
 * Created by user on 2017/9/23.
 */
public class SqlParser {
    public static List<EAItem> processFile(File file) {
        if (file == null || !file.canRead()) {
            return null;
        }

        List<EAItem> dbList = new ArrayList<EAItem>();

        // http://www.cnblogs.com/lovebread/archive/2009/11/23/1609122.html
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

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

                EAStereotypeEnum type = item.getStereotype();
                if (type == null) {
                    continue;
                }

                if (type.isDB()) {
                    db = item;
                    dbList.add(db);
                    System.out.println(db.toString());
                } else if (type.isTable()) {
                    table = item;
                    table.setParent(db);
                    System.out.println(table.toString());

                    // Mark the special tables
//                    String name = table.getName();
//                    if (name != null && (name.toLowerCase().endsWith("_log")
//                            || name.toLowerCase().endsWith("_logs"))) {
//                        table.setPhase(name);
//                    }

//                    String name = table.getName();
//                    if (name != null && name.toLowerCase().contains("_copy")) {
//                        table.setPhase(name);
//                    }
                } else if (type.isField()) {
                    item.setParent(table);

                    // Mark the special fields
//                    String name = item.getName();
//                    if ("company_id".equalsIgnoreCase(name) || "compay_id".equalsIgnoreCase(name) || "companyId".equalsIgnoreCase(name)) {
//                        table.setPhase(name);
//                    }

//                    String name = item.getName();
//                    if (name != null && name.toLowerCase().startsWith("is_delete")) {
//                        table.setPhase(name);
//                    }
                } else {
                    if (table != null) {
                        // Table's comment
                        table.setComment(item.getName());
                    } else {
                        System.out.printf("Un-supported item: %s\n", item.toString());
                    }
                }
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
                }
            }
        }

        return dbList;
    }

    public static EAItem parse(String sql, EAItem parent) {
        if (StrUtils.isEmpty(sql)) {
            return null;
        }

        String sqlLowercase = sql.toLowerCase();

        // Check DB, table, field and comment. Note: DB and comment are not at the beginning, while table and field are the first ones.
        if (sqlLowercase.indexOf(DB_Flag) >= 0) {
            // Remove the unused beginning.
            String name = sql.substring(sqlLowercase.indexOf(DB_Flag));
            name = parseName(name, DB_Splitter, DB_Index, DB_Trim_List, null);
            if (!StrUtils.isEmpty(name)) {
                return new EAItem(name, EATypeEnum.Artifact, EAStereotypeEnum.DB, parent);
            }
        }

        if (sqlLowercase.startsWith(Table_Flag)) {
            String name = sql.substring(sqlLowercase.indexOf(Table_Flag));
            name = parseName(name, Table_Splitter, Table_Index, Table_Trim_List, null);
            if (!StrUtils.isEmpty(name)) {
                return new EAItem(name, EATypeEnum.Class, EAStereotypeEnum.Table, parent);
            }
        }

        if (sqlLowercase.startsWith(Field_Flag)) {
            String name = sql.substring(sqlLowercase.indexOf(Field_Flag));
            name = parseName(name, Field_Splitter, Field_Index, Field_Trim_List, Field_Ignore_list);
            if (!StrUtils.isEmpty(name)) {
                EAItem item = new EAItem(name, EATypeEnum.Class, EAStereotypeEnum.Field, parent);

                // Find the comment of the field
                sql = sql.substring(sqlLowercase.indexOf(Field_Flag));
                sqlLowercase = sql.toLowerCase();
                if (sqlLowercase.indexOf(Comment_Flag) >= 0) {
                    name = sql.substring(sqlLowercase.indexOf(Comment_Flag));
                    name = parseName(name, Field_Comment_Splitter, Comment_Index, Comment_Trim_List, null);
                    if (!StrUtils.isEmpty(name)) {
                        item.setComment(name);
                    }
                }

                return item;
            }
        }

        if (sqlLowercase.indexOf(Comment_Flag) >= 0) {
            // Find the comment of the table, which is at the end of the definition block.
            String name = sql.substring(sqlLowercase.indexOf(Comment_Flag));
            name = parseName(name, Table_Comment_Splitter, Comment_Index, Comment_Trim_List, null);
            if (!StrUtils.isEmpty(name)) {
                return new EAItem(name, EATypeEnum.Class, EAStereotypeEnum.None, parent);
            }
        }

        return null;
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
        if (!StrUtils.isEmpty(name) && !ArrayUtils.isEmpty(trims)) {
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
        if (!StrUtils.isEmpty(name) && !ArrayUtils.isEmpty(ignores)) {
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

    public static String[] Name_Garbage_List = {"_copy"};

    // DB, Table, Field, Comment (of table and field)..
    public static String DB_Flag = "database";
    public static String DB_Splitter = ":";
    public static int DB_Index = 1;
    public static String[] DB_Trim_List = {" "};

    public static String Table_Flag = "create table";
    public static String Table_Splitter = " ";
    public static int Table_Index = 2;
    public static String[] Table_Trim_List = {"`"};

    public static String Field_Flag = "`";
    public static String Field_Splitter = " ";
    public static int Field_Index = 0;
    public static String[] Field_Trim_List = {"`"};
    public static String[] Field_Ignore_list = {
            "create_time", "create_by", "update_time", "update_by", "server_ip",
            "is_available", "is_disable",
            "version", "version_no", "client_versionno",
            "create_userid", "create_username", "create_userip", "create_usermac", "create_time_db",
            "update_userid", "update_username", "update_userip", "update_usermac", "update_time_db",
            "del_flg", "crt_id", "crt_time", "upd_id", "upd_time"
    };

    public static String Comment_Flag = "comment";
    public static String Field_Comment_Splitter = "'";
    public static String Table_Comment_Splitter = "'";
    public static int Comment_Index = 1;
    public static String[] Comment_Trim_List = {",", ";", "'", "\"", "()"};
}
