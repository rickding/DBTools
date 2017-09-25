package db2ea;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by user on 2017/9/23.
 */
public class DB2EA {
    public static void main(String[] args) {
        if (ArrayUtils.isEmpty(args)) {
            System.out.println("Please specify the MySQL dumped file or folder to convert multiple ones!");
            System.out.println("optional: -code-for-excel: specify to generate file for excel to parse.");
            System.out.println("optional: -separate-csv: specify to generate one csv file or multiple ones.");
            System.out.println("folder or *.sql file: specify the folder or sql file to parse.");
            return;
        }

        Date time_start = new Date();
        boolean codeForExcel = false;
        boolean separateCsv = false;

        // Read files
        for (String arg : args) {
            if (StrUtils.isEmpty(arg)) {
                continue;
            }

            if (arg.equals("-code-for-excel")) {
                codeForExcel = true;
                continue;
            } else if (arg.equals("-separate-csv")) {
                separateCsv = true;
                continue;
            }

            File file = new File(arg);
            if (!file.exists()) {
                continue;
            }

            // File or directory, while not iterate sub folders
            File[] files = null;
            if (file.isFile() && arg.toLowerCase().endsWith(SqlParser.File_SQL_Ext)) {
                files = new File[]{file};
            } else if (file.isDirectory()) {
                files = file.listFiles(new FilenameFilter() {
                    // @Override
                    public boolean accept(File dir, String name) {
                        return !StrUtils.isEmpty(name) && name.toLowerCase().endsWith(SqlParser.File_SQL_Ext);
                    }
                });
            }

            if (files == null || files.length <= 0) {
                continue;
            }

            // Remember the project and DB list.
            Map<String, List<EAItem>> projectMap = new HashMap<String, List<EAItem>>();
            for (File f : files) {
                List<EAItem> dbList = SqlParser.processFile(f);
                if (dbList != null && dbList.size() > 0) {
                    // Save the project and db list
                    projectMap.put(f.getPath(), dbList);
                }
            }

            // TODO: walk through the items to mark the project

            // Save the items
            EAWriter writer = null;
            if (!separateCsv) {
                writer = new EAWriter(file.getPath());
                writer.open();
            }

            for (Map.Entry<String, List<EAItem>> project : projectMap.entrySet()) {
                List<EAItem> dbList = project.getValue();
                if (dbList == null || dbList.size() <= 0) {
                    continue;
                }

                if (separateCsv) {
                    writer = new EAWriter(project.getKey());
                    writer.open();
                }

                for (EAItem db : dbList) {
                    if (db == null) {
                        continue;
                    }

                    db.saveToFile(writer, codeForExcel);
                }

                if (separateCsv) {
                    writer.close();
                }
            }

            if (!separateCsv) {
                writer.close();
            }
        }

        System.out.printf("Finished, start: %s, end: %s\n",
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
    }
}
