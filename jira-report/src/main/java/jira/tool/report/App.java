package jira.tool.report;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static String File_Ext = ".csv";
    public static String File_Name = ".xlsx";
    public static String Folder_name = "";

    public static void main(String[] args) {
        ExcelSample.excelSample();

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

        // Update files
        for (String filePath : filePaths) {
            if (StrUtils.isEmpty(filePath)) {
                continue;
            }

            File file = new File(filePath);
            if (!file.exists()) {
                continue;
            }

            // File or directory, while not iterate sub folders
            File[] files = null;
            if (file.isFile() && filePath.toLowerCase().endsWith(File_Ext)) {
                files = new File[]{file};
            } else if (file.isDirectory()) {
                files = file.listFiles(new FilenameFilter() {
                    // @Override
                    public boolean accept(File dir, String name) {
                        if (StrUtils.isEmpty(name)) {
                            return false;
                        }
                        String str = name.toLowerCase();
                        return str.endsWith(File_Ext) && !str.endsWith(File_Name);
                    }
                });
            }

            if (files == null || files.length <= 0) {
                continue;
            }

            // Update
            String outputFileName = null;
            String outputFolderName = null;
            for (File f : files) {
                if (file.isFile()) {
                    outputFileName = f.getPath();
                    if (outputFileName.toLowerCase().endsWith(File_Ext)) {
                        outputFileName = outputFileName.substring(0, filePath.length() - File_Ext.length());
                    }
                    outputFileName = String.format("%s%s", outputFileName, File_Name);
                } else if (file.isDirectory()) {
                    // Prepare the folder firstly
                    if (StrUtils.isEmpty(outputFolderName)) {
                        outputFolderName = String.format("%s%s", file.getPath(), Folder_name);
                        File folder = new File(outputFolderName);
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                    }

                    // Output file name
                    outputFileName = String.format("%s\\%s", outputFolderName, f.getName());
                    if (outputFileName.toLowerCase().endsWith(File_Ext)) {
                        outputFileName = outputFileName.substring(0, outputFileName.length() - File_Ext.length());
                    }

                    outputFileName = String.format("%s%s", outputFileName, File_Name);
                }

                XSSFWorkbook wb = new XSSFWorkbook();
                ExcelUtil.csvToExcel(wb.createSheet(), f.getPath());
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
