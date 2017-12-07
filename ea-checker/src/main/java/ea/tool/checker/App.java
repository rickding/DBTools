package ea.tool.checker;

import dbtools.common.file.CsvUtil;
import dbtools.common.file.ExcelUtil;
import dbtools.common.file.FileUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import ea.tool.api.EAUtil;
import jira.tool.ea.EA2JiraHeaderEnum;
import jira.tool.ea.JiraProjectEnum;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

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
    private static String File_Prefix = "";
    private static String File_Ext = ".eap";
    private static String File_Name = "%s-implemented-%d-pre-create-story-%d.xlsx";
    private static String Folder_name = "";
    private static String strToday = DateUtils.format(new Date(), "MMdd");

    public static void main(String[] args) {
        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容提交文件夹");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容提交文件夹\\商家线");
            add("C:\\Work\\doc\\30-项目-PMO\\需求内容提交文件夹\\海航ERP需求管理");
            add("..\\");
            add("..\\商家线");
            add("..\\海航ERP需求管理");
            add(".\\");
            add(".\\商家线");
            add(".\\海航ERP需求管理");
        }};

        if (args != null) {
            for (String arg : args) {
                if (!StrUtils.isEmpty(arg)) {
                    filePaths.add(arg);
                }
            }
        }

        // Process files
        List<String> projects = new ArrayList<String>();
        boolean isCsv = File_Ext.toLowerCase().endsWith(".csv");

        XSSFWorkbook wb = new XSSFWorkbook();
        if (wb == null) {
            return;
        }

        File outputFile = null;
        int implementedCount = 0;
        int preCreateCount = 0;

        Map<String, List<String[]>> teamStoryListMap = new HashMap<String, List<String[]>>();
        List<String> preCreatedStoryList = new ArrayList<String>();

        for (String filePath : filePaths) {
            File file = new File(filePath);
            File[] files = FileUtils.findFiles(filePath, File_Prefix, File_Ext, File_Name);
            if (!file.exists() || files == null || files.length <= 0) {
                continue;
            }

            // Remember the first filePath to save the output file
            if (outputFile == null) {
                outputFile = file;
            }

            for (File f : files) {
                // Check the date in file name
                String fileName = f.getName();

                if (isCsv) {
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
                }

                // Find project
                JiraProjectEnum project = JiraProjectEnum.findProject(fileName);
                if (project == null) {
                    System.out.printf("Can't find project definition: %s\n", fileName);
                    continue;
                }

                // Read file and fill excel
                System.out.printf("Start to read: %s\n", fileName);
                List<String[]> records = isCsv ? CsvUtil.readFile(f.getPath()) : EAUtil.getElementList(f.getPath());
                EACheckUtil.formatDate(records);
                ExcelUtil.fillSheet(ExcelUtil.getOrCreateSheet(wb, f.getName()), records);

                // Process
                EACheckUtil.process(project, records, teamStoryListMap, preCreatedStoryList, null);
                projects.add(f.getPath());
            }
        }

        // Save the existed story id for label
        if (preCreatedStoryList != null && preCreatedStoryList.size() > 0) {
            preCreateCount += preCreatedStoryList.size();
        }

        // Fill stories to wb
        if (teamStoryListMap != null && teamStoryListMap.size() > 0) {
            // Get the headers
            String[] headers = EAUtil.getHeaders();

            // Write data to excel
            for (Map.Entry<String, List<String[]>> teamStories : teamStoryListMap.entrySet()) {
                List<String[]> stories = teamStories.getValue();
                implementedCount += stories.size();

                stories.add(0, headers);
                ExcelUtil.fillSheet(ExcelUtil.getOrCreateSheet(wb, String.format("%s-%d", teamStories.getKey(), stories.size() - 1)), stories);
            }
        }

        // Save file
        String outputFileName = null;
        if (outputFile != null) {
            outputFileName = FileUtils.getOutputFileName(outputFile, "", File_Ext, String.format(File_Name, strToday, implementedCount, preCreateCount), Folder_name);
            ExcelUtil.saveToFile(wb, outputFileName);
        }

        System.out.printf("Finished %d folder(s), %d file(s), start: %s, end: %s\r\n",
                filePaths.size(),
                projects.size(),
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
        for (String project : projects) {
            System.out.println(project);
        }

        System.out.printf("\r\nSaved to: %s\r\n", outputFileName);
    }
}
