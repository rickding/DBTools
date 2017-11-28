package jira.tool.jira;

import dbtools.common.file.CsvUtil;
import dbtools.common.file.FileUtils;
import dbtools.common.file.FileWriter;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.ea.EAElementUtil;
import jira.tool.ea.JiraProjectEnum;

import java.io.File;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private static String Jira_File = "EA-PMO-all (上海欧电云信息科技有限公司).csv";
    private static String Sql_File_Name = "%s-update-guid-%d.sql";
    private static String strToday = DateUtils.format(new Date(), "MMdd");

    private static String File_Prefix = "";
    private static String File_Ext = ".csv";
    private static String File_Name = "%s-story-key-%s-%d.csv";
    private static String Folder_name = "";

    public static void main(String[] args) {
        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
            add(".\\");
//            add("C:\\Work\\doc\\30-项目-PMO\\需求内容确认文件夹\\jira_transfer\\1128");
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
                if (guidStoryMap != null && guidStoryMap.size() > 0) {
                    break;
                }
            }
        }

        // Process files
        List<String> projects = new ArrayList<String>();
        for (String filePath : filePaths) {
            File file = new File(filePath);
            File[] files = FileUtils.findFiles(filePath, File_Prefix, File_Ext, File_Name);
            if (!file.exists() || files == null || files.length <= 0) {
                continue;
            }

            // Update and save
            Map<String, String> noGuidFromJiraMap = new HashMap<String, String>();
            for (File f : files) {
                String fileName = f.getName();

                // Read date in the file name
                int extIndex = fileName.indexOf(File_Ext);
                if (extIndex < 4) {
                    continue;
                }

                // TODO: Skip the old files
                String strDate = fileName.substring(extIndex - 4, extIndex);
                if (strDate.compareTo("1123") < 0) {
                    continue;
                }

                // Find project
                JiraProjectEnum project = JiraProjectEnum.findProject(f.getName());
                if (project == null) {
                    System.out.printf("Can't find project definition: %s\n", fileName);
                    continue;
                }

                // Read file
                List<String[]> elements = CsvUtil.readFile(f.getPath());
                if (elements == null || elements.size() <= 1) {
                    continue;
                }

                // Process
                elements = Jira2EA.updateStoryToElement(elements, guidStoryMap, noGuidFromJiraMap);
                if (elements != null) {
                    // Only the needed values
                    int storyCount = EAElementUtil.countRequirements(elements, true);
                    if (storyCount > 0) {
                        elements = Jira2EA.getSavedValues(elements);

                        // Save file
                        String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, String.format(File_Name, strToday, project.getPrefixes()[0], storyCount), Folder_name);
                        CsvUtil.saveToFile(elements, outputFileName);
                        projects.add(f.getPath());
                    }
                } else {
                    System.out.printf("Fail to process file: %s\n", f.getPath());
                }
            }

            // Generate sql
            String[] sqlArray = Jira2EA.generateUpdateJiraGUIDSSQL(noGuidFromJiraMap, issueKeyIdMap);
            if (sqlArray != null && sqlArray.length > 1) {
                String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, String.format(Sql_File_Name, strToday, sqlArray.length - 2), Folder_name);
                FileWriter writer = new FileWriter(outputFileName);

                if (writer.open()) {
                    writer.writeLines(sqlArray);
                }
                writer.close();
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
