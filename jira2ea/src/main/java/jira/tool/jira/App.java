package jira.tool.jira;

import dbtools.common.file.CsvUtil;
import dbtools.common.file.FileUtils;
import dbtools.common.file.FileWriter;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;

import java.io.File;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private static String Jira_File = "EA-PMO-all (上海欧电云信息科技有限公司).csv";
    private static String Sql_File_Name = ".sql";

    private static String File_Prefix = "jira-transfer";
    private static String File_Ext = ".csv";
    private static String File_Name = "_story_id.csv";
    private static String Folder_name = "";

    public static void main(String[] args) {
        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
//            add(".\\");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容确认文件夹");
        }};

        if (args != null) {
            for (String arg : args) {
                if (!StrUtils.isEmpty(arg)) {
                    filePaths.add(arg);
                }
            }
        }

        // Get the guid story key map firstly
        Map<String, String> guidStoryMap = null;
        Map<String, String> issueKeyIdMap = new HashMap<String, String>();

        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.isDirectory()) {
                guidStoryMap = Jira2EA.getGUIDStoryMap(String.format("%s\\%s", filePath, Jira_File), issueKeyIdMap);
                if (guidStoryMap != null) {
                    break;
                }
            }
        }

        List<String> projects = new ArrayList<String>();
        // Process files
        for (String filePath : filePaths) {
            File file = new File(filePath);
            File[] files = FileUtils.findFiles(filePath, File_Prefix, File_Ext, File_Name);
            if (!file.exists() || files == null || files.length <= 0) {
                continue;
            }

            // Update and save
            for (File f : files) {
                // Read file
                List<String[]> elements = CsvUtil.readFile(f.getPath());
                if (elements == null || elements.size() <= 1) {
                    continue;
                }

                // Process
                Map<String, String> noGuidFromJiraMap = new HashMap<String, String>();
                elements = Jira2EA.updateStoryToElement(elements, guidStoryMap, noGuidFromJiraMap);
                if (elements != null) {
                    elements = Jira2EA.getSavedValues(elements);

                    // Save file
                    String outputFileName = FileUtils.getOutputFileName(file, f, File_Ext, File_Name, Folder_name);
                    CsvUtil.saveToFile(elements, outputFileName);
                    projects.add(f.getPath());

                    // Generate sql
                    String[] sqlArray = Jira2EA.generateUpdateJiraGUIDSSQL(noGuidFromJiraMap, issueKeyIdMap);
                    if (!ArrayUtils.isEmpty(sqlArray)) {
                        outputFileName = FileUtils.getOutputFileName(file, f, File_Ext, Sql_File_Name, Folder_name);
                        FileWriter writer = new FileWriter(outputFileName);

                        if (writer.open()) {
                            writer.writeLines(sqlArray);
                        }
                        writer.close();
                    }
                } else {
                    System.out.printf("Fail to process file: %s\n", f.getPath());
                }
            }
        }

        System.out.printf("Finished %d folder(s), %d file(s), start: %s, end: %s\n",
                filePaths.size(),
                projects.size(),
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );

        System.out.println("\nThe processed files:");
        for (String project : projects) {
            System.out.println(project);
        }
    }
}
