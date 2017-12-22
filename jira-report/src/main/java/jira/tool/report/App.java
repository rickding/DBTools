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
    public static String File_Ext = ".xlsx";
    public static String Folder_name = "";

    public static void main(String[] args) {
        Date time_start = new Date();
        Map<String, BaseReport[]> filePaths = new HashMap<String, BaseReport[]>() {{
            put("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\Jira统计日报", new BaseReport[]{new DailyDueDateReport(), new DailyDevFinishReport()});
            put("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\交付计划半周报", new BaseReport[]{new ReleasePlanReport()});
            put("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\PMO周报", new BaseReport[]{new WeeklyReleaseReport(), new WeeklyStartPlanReport(), new WeeklyReleasePlanReport()});
//            put("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\版本发布会", new BaseReport[]{new QAReleasePlanReport()});
        }};

//        for (Map.Entry<String, BaseReport[]> filePath : filePaths.entrySet()) {
//            filePaths.clear();
//            filePaths.put(".\\", filePath.getValue());
//        }

        System.out.println("Start ...");
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
