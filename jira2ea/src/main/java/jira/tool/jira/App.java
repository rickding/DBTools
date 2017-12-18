package jira.tool.jira;

import com.rms.db.ElementUtil;
import com.rms.db.model.ElementEx;
import dbtools.common.file.CsvUtil;
import dbtools.common.file.FileUtils;
import dbtools.common.file.FileWriter;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import ea.tool.api.EAElementUtil;
import ea.tool.api.EAFile;
import jira.tool.db.JiraStoryUtil;
import jira.tool.ea.EADateUtil;
import jira.tool.ea.JiraProjectEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Hello world!
 */
public class App {
    private static String strToday = DateUtils.format(new Date(), "MMdd");
    private static String Sql_File_Name = "%s-jira2ea-update-guid-%d.sql";

    private static String File_Prefix = "";
    private static String File_Ext = ".eap";
    private static String File_Name = "%s-jira2ea-%s-%d.csv";
    private static String Folder_name = "";

    public static void main(String[] args) {
        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
            add("..\\");
            add("..\\商家线");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容确认文件夹");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容提交文件夹");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容提交文件夹\\商家线");
        }};

        if (args != null) {
            for (String arg : args) {
                if (!StrUtils.isEmpty(arg)) {
                    filePaths.add(arg);
                }
            }
        }

        // Get the guid story key map firstly
        Map<String, String[]> keyStoryMap = new HashMap<String, String[]>();
        Map<String, String> guidKeyMap = JiraStoryUtil.getGUIDKeyMap(filePaths, keyStoryMap, null);
        Map<String, ElementEx> guidElementMapFromRMS = ElementUtil.getGuidElementMap();

        // Process files
        List<String> projects = new ArrayList<String>();
        boolean isCsv = File_Ext.toLowerCase().endsWith(".csv");
        EAFile eaFile = new EAFile();

        for (String filePath : filePaths) {
            File file = new File(filePath);
            File[] files = FileUtils.findFiles(filePath, File_Prefix, File_Ext, File_Name);
            if (!file.exists() || files == null || files.length <= 0) {
                continue;
            }

            // Update and save
            Map<String, String> noGuidFromJiraMap = new HashMap<String, String>();
            for (File f : files) {
                // Find project
                String fileName = f.getName();
                JiraProjectEnum project = JiraProjectEnum.findProject(fileName);
                if (project == null) {
                    continue;
                }

                // Read file
                System.out.printf("Start to read: %s\r\n", fileName);
                List<String[]> elements = null;
                if (isCsv) {
                    elements = CsvUtil.readFile(f.getPath());
                    EADateUtil.formatDate(elements);
                } else {
                    eaFile.open(f.getPath());
                    elements = eaFile.getElementList();
                }
                if (elements == null || elements.size() <= 1) {
                    eaFile.close();
                    continue;
                }

                // Process
                elements = Jira2EA.updateStoryInfoIntoElement(elements, guidKeyMap, keyStoryMap, noGuidFromJiraMap, isCsv, guidElementMapFromRMS);
                if (elements != null) {
                    // Only the needed values
                    int storyCount = EAElementUtil.countRequirements(elements, true);
                    if (storyCount > 0) {
                        // Update into ea file
                        eaFile.updateStoryInfo(elements);

                        // Get values to save csv file
                        elements = Jira2EA.getSavedValues(elements);

                        // Save file
                        String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, String.format(File_Name, strToday, project.getPrefixes()[0], storyCount), Folder_name);
                        CsvUtil.saveToFile(elements, outputFileName);
                        projects.add(f.getPath());
                    }
                } else {
                    System.out.printf("Fail to process file: %s\n", f.getPath());
                }
                eaFile.close();
            }

            // Generate sql
            String[] sqlArray = Jira2EA.generateUpdateJiraGUIDSQL(noGuidFromJiraMap, keyStoryMap);
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
