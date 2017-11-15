package jira.tool.report;

import dbtools.common.file.FileUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.report.processor.BaseReport;
import jira.tool.report.processor.SprintReport;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
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

        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
            add("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\交付计划");
        }};

        if (args != null) {
            for (String arg : args) {
                if (StrUtils.isEmpty(arg)) {
                    continue;
                }
                filePaths.add(arg);
            }
        }

        List<String> projects = new ArrayList<String>();
        BaseReport report = new SprintReport();

        // Update files
        for (String filePath : filePaths) {
            File file = new File(filePath);
            File[] files = FileUtils.findFiles(filePath, File_Ext, File_Name);
            if (!file.exists() || files == null || files.length <= 0) {
                continue;
            }

            // Update and save
            for (File f : files) {
                XSSFWorkbook wb = new XSSFWorkbook();
                String sheetName = report.getSheetName("data");
                XSSFSheet sheet = StrUtils.isEmpty(sheetName) ? wb.createSheet() : wb.createSheet(sheetName);
                ExcelUtil.csvToExcel(sheet, f.getPath(), report);

                String outputFileName = FileUtils.getOutputFileName(file, f, File_Ext, File_Name, Folder_name);
                ExcelUtil.saveToFile(wb, outputFileName);

                projects.add(f.getPath());
            }
        }

        System.out.printf("Finished %d folders, %d files, start: %s, end: %s\n",
                filePaths.size(),
                projects.size(),
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
    }
}
