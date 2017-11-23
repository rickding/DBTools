package jira.tool.ea;

import dbtools.common.file.CsvUtil;
import dbtools.common.file.ExcelUtil;
import dbtools.common.file.FileUtils;
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
    public static String File_Prefix = "";
    public static String File_Ext = ".csv";
    public static String File_Name = "jira-transfer-%s.xlsx";
    public static String Folder_name = "";

    public static String Sheet_EA = "ea";

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

        List<String> projects = new ArrayList<String>();
        String strToday = DateUtils.format(new Date(), "MMdd");

        // Process files
        for (String filePath : filePaths) {
            File file = new File(filePath);
            File[] files = FileUtils.findFiles(filePath, File_Prefix, File_Ext, File_Name);
            if (!file.exists() || files == null || files.length <= 0) {
                continue;
            }

            // Update and save
            XSSFWorkbook wb = new XSSFWorkbook();
            if (wb == null) {
                continue;
            }

            Map<String, List<String[]>> teamStoryListMap = new HashMap<String, List<String[]>>();
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
                ExcelUtil.fillSheet(ExcelUtil.getOrCreateSheet(wb, Sheet_EA), records);

                // Process
                EA2Jira.process(project, records, teamStoryListMap);
                projects.add(f.getPath());
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

                // Write to excel
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
