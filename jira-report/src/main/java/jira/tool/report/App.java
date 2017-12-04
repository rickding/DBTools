package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.file.FileUtils;
import dbtools.common.utils.DateUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Hello world!
 */
public class App {
    public static String File_Ext = ".csv";
    public static String Folder_name = "";

    public static void main(String[] args) {
        Date time_start = new Date();
        Map<String, BaseReport[]> filePaths = new HashMap<String, BaseReport[]>() {{
//            add(".\\");
//            add("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\Jira统计日报");
//            add("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\交付计划");
            put("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\PMO周报", new BaseReport[]{new WeeklyReleaseReport()});
        }};

        List<String> projects = new ArrayList<String>();

        // Process files
        for (Map.Entry<String, BaseReport[]> fileReport : filePaths.entrySet()) {
            String filePath = fileReport.getKey();
            File file = new File(filePath);

            // Update and save
            for (BaseReport report : fileReport.getValue()) {
                XSSFWorkbook wb = report.getWorkbook(filePath);
                if (wb == null) {
                    continue;
                }

                // Data
                report.fillDataSheets(wb);

                // Save file
                String outputFileName = FileUtils.getOutputFileName(file, "", File_Ext, report.getFileName(), Folder_name);
                ExcelUtil.saveToFile(wb, outputFileName);

                projects.add(report.getFileName());
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
