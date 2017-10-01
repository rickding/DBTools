package dbtools.sqlupdater;

import dbtools.common.file.FileReader;
import dbtools.common.file.FileWriter;
import dbtools.common.utils.StrUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2017/9/30.
 */
public class SqlUpdater {
    public static String Sql_File_Ext = ".sql";
    public static String Sql_File_name = "_updated.sql";

    private static String[] Sql_Update_Header_Array = {
            "set @companyId = 1000;",
            "-- Original 51, replace:",
            "-- ,51, => ,@companyId,",
            "-- ,51) => ,@companyId)};",
            ""
    };

    private static Map<String, String> Sql_Replace_Map = new HashMap<String, String>() {{
        put(",51,", ",@companyId,");
        put(",51)", ",@companyId)");
    }};


    public static void processFile(File file) {
        if (file == null || !file.canRead()) {
            return;
        }

        // Input
        String filePath = file.getPath();
        FileReader reader = new FileReader(filePath);
        if (!reader.open()) {
            System.out.printf("Fail to open file: %s\n", filePath);
            return;
        }

        // Output
        if (filePath.toLowerCase().endsWith(Sql_File_Ext)) {
            filePath = filePath.substring(0, filePath.length() - Sql_File_Ext.length());
        }
        filePath = String.format("%s%s", filePath, Sql_File_name);

        FileWriter writer = new FileWriter(filePath);
        if (!writer.open()) {
            System.out.printf("Fail to create output file: %s\n", filePath);

            // Close
            reader.close();
            return;
        }

        writeHeader(writer);

        // read and update, then write
        String str;
        while ((str = reader.readLine()) != null) {
            if (!StrUtils.isEmpty(str)) {
                // Update the str;
                str = updateSql(str);
            }

            // Write
            writer.writeLine(str);
        }

        // close
        writer.close();
        reader.close();
    }

    private static String updateSql(String sql) {
        if (StrUtils.isEmpty(sql)) {
            return sql;
        }

        for (Map.Entry<String, String> replace : Sql_Replace_Map.entrySet()) {
            String str = replace.getKey();
            if (StrUtils.isEmpty(str)) {
                continue;
            }

            sql = sql.replace(str, replace.getValue());
        }
        return sql;
    }

    private static void writeHeader(FileWriter writer) {
        if (writer == null || !writer.isOpen()) {
            return;
        }

        for (String str : Sql_Update_Header_Array) {
            writer.writeLine(str);
        }
    }
}
