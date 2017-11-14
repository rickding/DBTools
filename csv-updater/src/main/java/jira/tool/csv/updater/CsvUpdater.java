package jira.tool.csv.updater;

import dbtools.common.file.FileUpdater;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CsvUpdater {
    public static String File_Ext = ".csv";
    public static String File_Name = "_updated.csv";
    public static String Folder_name = "_updated";

    // Check if the tail existed.
    public static String Tail_Flag = "";

    // Write the header into file
    private static long Update_Header_Line_Index = -1;
    private static String[] Update_Header_Array = {
    };

    // Replace the special strings
    private static Map<String, String> Replace_Map = new HashMap<String, String>() {{
        put(",Android,", ",APP,");
        put(",IOS,IOS,", ",IOS,APP,");

        put(",WMS,", ",供应链,");
        put(",史泰博,", ",供应链,");
        put(",互联网+,", ",供应链,");

        put(",分销,", ",商家线,");
        put(",用户线,", ",商家线,");
        put(",雨燕平台,", ",基础架构,");
    }};

    // Some special strings are replaced wrongly, which should be restored.
    private static Map<String, String> Restore_Map = new HashMap<String, String>() {{
    }};

    public static void processFile(File inputFile, String outputFileName) {
        FileUpdater.processFile(inputFile, outputFileName, Replace_Map, Restore_Map, Update_Header_Line_Index, Update_Header_Array, Tail_Flag);
    }
}
