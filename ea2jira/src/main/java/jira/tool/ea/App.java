package jira.tool.ea;

import dbtools.common.file.CsvUtil;
import dbtools.common.file.ExcelUtil;
import dbtools.common.file.FileUtils;
import dbtools.common.file.FileWriter;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private static String File_Prefix = "";
    private static String File_Ext = ".csv";
    private static String File_Name = "jira-transfer-%s-create-story.xlsx";
    private static String Folder_name = "";

    private static String Jira_File = "EA-PMO-all (上海欧电云信息科技有限公司).csv";
    private static String Lable_File_Name = "jira-transfer-%s-add-label.txt";
    private static String Sheet_EA = "ea-%s";
    private static String strToday = DateUtils.format(new Date(), "MMdd");

    public static void main(String[] args) {
        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
//            add(".\\");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容确认文件夹\\jira_transfer");
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
        Set<String> pmoLabelKeySet = new HashSet<String>();

        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.isDirectory()) {
                guidStoryMap = JiraStoryUtil.getGUIDStoryMap(String.format("%s\\%s", filePath, Jira_File), null, pmoLabelKeySet);
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

            XSSFWorkbook wb = new XSSFWorkbook();
            if (wb == null) {
                continue;
            }

            Map<String, List<String[]>> teamStoryListMap = new HashMap<String, List<String[]>>();
            List<String> preCreatedStoryList = new ArrayList<String>();

            for (File f : files) {
                // Check the date in file name
                String fileName = f.getName();

                // Read date in the file name
                int extIndex = fileName.indexOf(File_Ext);
                if (extIndex < 4) {
                    continue;
                }

                // Skip the old files
                String strDate = fileName.substring(extIndex - 4, extIndex);
                if (strDate.compareTo(strToday) != 0) {
                    continue;
                }

                // Find project
                JiraProjectEnum project = JiraProjectEnum.findProject(fileName);
                if (project == null) {
                    System.out.printf("Can't find project definition: %s\n", fileName);
                    continue;
                }

                // Read file and fill to excel
                List<String[]> records = CsvUtil.readFile(f.getPath());
                ExcelUtil.fillSheet(ExcelUtil.getOrCreateSheet(wb, String.format(Sheet_EA, f.getName())), records);

                // Process
                EA2Jira.process(project, records, teamStoryListMap, guidStoryMap, preCreatedStoryList, pmoLabelKeySet);
                projects.add(f.getPath());
            }

            // Save the existed story id for label
            if (preCreatedStoryList != null && preCreatedStoryList.size() > 0) {
                String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, String.format(Lable_File_Name, strToday), Folder_name);
                FileWriter writer = new FileWriter(outputFileName);

                if (writer.open()) {
                    writer.writeLine(String.format("Total: %d", preCreatedStoryList.size()));
                    writer.writeLine(preCreatedStoryList.toString());
                }
                writer.close();
            }

            // Fill stories to wb
            if (teamStoryListMap != null && teamStoryListMap.size() > 0) {
                // Get the headers
                JiraHeaderEnum[] jiraHeaders = JiraHeaderEnum.getSortedHeaders();
                String[] headers = new String[jiraHeaders.length];
                int i = 0;
                for (JiraHeaderEnum jiraHeader : jiraHeaders) {
                    headers[i++] = jiraHeader.getCode();
                }

                // Write data to excel
                for (Map.Entry<String, List<String[]>> teamStories : teamStoryListMap.entrySet()) {
                    List<String[]> stories = teamStories.getValue();
                    stories.add(0, headers);
                    ExcelUtil.fillSheet(ExcelUtil.getOrCreateSheet(wb, teamStories.getKey()), stories);
                }

                // Save file
                String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, String.format(File_Name, strToday), Folder_name);
                ExcelUtil.saveToFile(wb, outputFileName);
            }
        }

        System.out.printf("Finished %d folder(s), %d file(s), start: %s, end: %s\n",
                filePaths.size(),
                projects.size(),
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
        for (String project : projects) {
            System.out.println(project);
        }
    }
}
