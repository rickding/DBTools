package dbtools.sqlupdater;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Specify the MySQL dumped file or folder to update:");
        System.out.println("folder or *.sql file: one or multiple ones, to specify the folder or sql file to update.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>() {{
            add("E:\\work\\doc\\17-应用架构部\\03-部门管理\\prod1.0\\db\\dump_saas2.0_test");
        }};

        for (String arg : args) {
            if (StrUtils.isEmpty(arg)) {
                continue;
            }
            filePaths.add(arg);
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
            if (file.isFile() && filePath.toLowerCase().endsWith(SqlUpdater.File_Ext)) {
                files = new File[]{file};
            } else if (file.isDirectory()) {
                files = file.listFiles(new FilenameFilter() {
                    // @Override
                    public boolean accept(File dir, String name) {
                        return !StrUtils.isEmpty(name) && name.toLowerCase().endsWith(SqlUpdater.File_Ext);
                    }
                });
            }

            if (files == null || files.length <= 0) {
                continue;
            }

            // Update
            for (File f : files) {
                SqlUpdater.processFile(f);
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
