package jira.tool.report;

import dbtools.common.file.FileUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static String File_Ext = ".csv";
    public static String File_Name = ".xlsx";
    public static String Folder_name = "";

    public static void main(String[] args) {
//        ExcelSample.excelSample();
//        DB.getDb().getMapper(UserMapper.class).countUser();

        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
            add(".\\");
//            add("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\Jira统计日报");
//            add("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\交付计划");
//            add("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\PMO周报");
        }};

        if (args != null) {
            for (String arg : args) {
                if (!StrUtils.isEmpty(arg)) {
                    filePaths.add(arg);
                }
            }
        }

        List<String> projects = new ArrayList<String>();

        // Process files
        for (String filePath : filePaths) {
            File file = new File(filePath);
            File[] files = FileUtils.findFiles(filePath, File_Ext, File_Name);
            if (!file.exists() || files == null || files.length <= 0) {
                continue;
            }

            // Update and save
            for (File f : files) {
                BaseReport report = BaseReport.getReport(f.getName());
                String templateName = report.getTemplateName();

                XSSFWorkbook wb = null;
                if (StrUtils.isEmpty(templateName)) {
                    wb = new XSSFWorkbook();
                } else {
                    // Open the template
                    templateName = String.format("%s\\%s", filePath, templateName);
                    try {
                        wb = new XSSFWorkbook(templateName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (wb == null) {
                    continue;
                }

                // Data
                report.fillDataSheets(wb, new String[] {f.getPath()});
                report.fillDataSheets(wb);

                // Save file
                String outputFileName = FileUtils.getOutputFileName(file, f, File_Ext, File_Name, Folder_name);
                ExcelUtil.saveToFile(wb, outputFileName);

                projects.add(f.getPath());
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
