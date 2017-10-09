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
    public static String Sql_Folder_name = "_updated";

    private static long Sql_Update_Header_Line_Index = 17;
    private static String[] Sql_Update_Header_Array = {
            "set @companyId = 1000;",
            "-- Original 51 is replaced:",
            "-- ,51, => ,@companyId,",
            "-- ,51) => ,@companyId)",
            ""
    };

    private static Map<String, String> Sql_Replace_Map = new HashMap<String, String>() {{
        put(",51,", ",@companyId,");
        put(",51)", ",@companyId)");
    }};


    public static void processFile(File inputFile, String outputFileName) {
        if (inputFile == null || !inputFile.exists() || !inputFile.canRead() || StrUtils.isEmpty(outputFileName)) {
            return;
        }

        // Input
        String filePath = inputFile.getPath();
        FileReader reader = new FileReader(filePath);
        if (!reader.open()) {
            System.out.printf("Fail to open file: %s\n", filePath);
            return;
        }

        // Output
        FileWriter writer = new FileWriter(outputFileName);
        if (!writer.open()) {
            System.out.printf("Fail to create output file: %s\n", outputFileName);

            // Close
            reader.close();
            return;
        }

        // Mark the header written or not
        long index = 0;
        boolean headerWritten = false;

        // read and update, then write
        String str;
        while ((str = reader.readLine()) != null) {
            if (!StrUtils.isEmpty(str)) {
                // Update the str;
                str = updateSql(str);
            }

            // Write header
            if (!headerWritten && Sql_Update_Header_Line_Index <= index++) {
                headerWritten = true;
                index += writeHeaders(writer);
            }

            // Write line
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

    private static long writeHeaders(FileWriter writer) {
        if (writer == null || !writer.isOpen()) {
            return 0;
        }

        for (String str : Sql_Update_Header_Array) {
            writer.writeLine(str);
        }
        return Sql_Update_Header_Array.length;
    }
}
