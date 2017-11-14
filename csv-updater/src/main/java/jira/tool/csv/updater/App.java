package jira.tool.csv.updater;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("Specify the file or folder to update:");
        System.out.println("folder or file: one or multiple ones, to specify the one(s) to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
            add("C:\\Work\\doc\\30-项目-PMO\\PMO报表\\Jira统计日报");
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
            if (file.isFile() && filePath.toLowerCase().endsWith(CsvUpdater.File_Ext)) {
                files = new File[]{file};
            } else if (file.isDirectory()) {
                files = file.listFiles(new FilenameFilter() {
                    // @Override
                    public boolean accept(File dir, String name) {
                        if (StrUtils.isEmpty(name)) {
                            return false;
                        }
                        String str = name.toLowerCase();
                        return str.endsWith(CsvUpdater.File_Ext) && !str.endsWith(CsvUpdater.File_Name);
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
                    if (outputFileName.toLowerCase().endsWith(CsvUpdater.File_Ext)) {
                        outputFileName = outputFileName.substring(0, filePath.length() - CsvUpdater.File_Ext.length());
                    }
                    outputFileName = String.format("%s%s", outputFileName, CsvUpdater.File_Name);
                } else if (file.isDirectory()) {
                    // Prepare the folder firstly
                    if (StrUtils.isEmpty(outputFolderName)) {
                        outputFolderName = String.format("%s%s", file.getPath(), CsvUpdater.Folder_name);
                        File folder = new File(outputFolderName);
                        if (!folder.exists()) {
                            folder.mkdir();
                        }
                    }

                    outputFileName = String.format("%s\\%s", outputFolderName, f.getName());
                    if (!outputFileName.toLowerCase().endsWith(CsvUpdater.File_Ext)) {
                        outputFileName = String.format("%s%s", outputFileName, CsvUpdater.File_Ext);
                    }
                }

                CsvUpdater.processFile(f, outputFileName);
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
