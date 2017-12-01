package jira.tool.ea;

import dbtools.common.file.CsvUtil;
import dbtools.common.file.ExcelUtil;
import dbtools.common.file.FileUtils;
import dbtools.common.file.FileWriter;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    private static String File_Prefix = "";
    private static String File_Ext = ".csv";
    private static String File_Name = "%s-create-story-%d.xlsx";
    private static String Team_File_name = "%s-create-story-%s-%d.csv";
    private static String Folder_name = "";

    private static String strToday = DateUtils.format(new Date(), "MMdd");
    private static String Label_File_Name = "%s-add-label-%d.txt";
    private static String Sheet_EA = "ea-%s";

    public static void main(String[] args) {
        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
//            add(".\\");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容确认文件夹\\jira_transfer\\1128");
        }};

        if (args != null) {
            for (String arg : args) {
                if (!StrUtils.isEmpty(arg)) {
                    filePaths.add(arg);
                }
            }
        }

        // Get the guid story key map firstly
        Set<String> pmoLabelKeySet = new HashSet<String>();
        Map<String, String> guidStoryMap = JiraStoryUtil.getGUIDKeyMap(filePaths, null, pmoLabelKeySet);

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
                String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, String.format(Label_File_Name, strToday, preCreatedStoryList.size()), Folder_name);
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
                EA2JiraHeaderEnum[] jiraHeaders = EA2JiraHeaderEnum.getSavedHeaders();
                String[] headers = new String[jiraHeaders.length];
                int i = 0;
                for (EA2JiraHeaderEnum jiraHeader : jiraHeaders) {
                    headers[i++] = jiraHeader.getCode();
                }

                // Write data to excel
                int count = 0;
                for (Map.Entry<String, List<String[]>> teamStories : teamStoryListMap.entrySet()) {
                    List<String[]> stories = teamStories.getValue();
                    count += stories.size();

                    stories.add(0, headers);
                    ExcelUtil.fillSheet(ExcelUtil.getOrCreateSheet(wb, String.format("%s-%d", teamStories.getKey(), stories.size() - 1)), stories);

                    // Save separate csv files
                    List<String> teams = new ArrayList<String>(2);
                    String team = teamStories.getKey();
                    if ("APP".equalsIgnoreCase(team)) {
                        teams.add("IOS");
                        teams.add("APPA");
                    } else {
                        teams.add(team);
                    }
                    
                    for (String tmp : teams) {
                        String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, String.format(Team_File_name, strToday, tmp, stories.size() - 1), Folder_name);
                        CsvUtil.saveToFile(stories, outputFileName);
                    }
                }

                // Save file
                String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, String.format(File_Name, strToday, count), Folder_name);
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
