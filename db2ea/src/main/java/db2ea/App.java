package db2ea;

import db2ea.enums.ProjectEnum;
import db2ea.utils.ArrayUtils;
import db2ea.utils.DateUtils;
import db2ea.utils.StrUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created by user on 2017/9/23.
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Specify the MySQL dumped file or folder to convert:");
        System.out.println("optional: -code-for-excel: specify to generate file for excel to parse.");
        System.out.println("optional: -separate-csv: specify to generate one csv file or multiple ones.");
        System.out.println("folder or *.sql file: one or multiple ones, to specify the folder or sql file to parse.");

        Date time_start = new Date();
        boolean codeForExcel = false;
        boolean separateCsv = false;

        Set<String> filePaths = new HashSet<String>(){{
            add(ProjectEnum.HHPLUS_DEV.getPath());
            add(ProjectEnum.PROD_DEV.getPath());
        }};

        for (String arg : args) {
            if (StrUtils.isEmpty(arg)) {
                continue;
            }

            if (arg.equals("-code-for-excel")) {
                codeForExcel = true;
            } else if (arg.equals("-separate-csv")) {
                separateCsv = true;
            } else {
                filePaths.add(arg);
            }
        }

        // The project and DB list
        List<Project> projects = new ArrayList<Project>();

        // Parse files
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
            if (file.isFile() && filePath.toLowerCase().endsWith(SqlParser.File_SQL_Ext)) {
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

            // Check the project and DB list.
            Map<String, List<EAItem>> projectMap = new HashMap<String, List<EAItem>>();
            for (File f : files) {
                List<EAItem> dbList = SqlParser.processFile(f);
                if (dbList != null && dbList.size() > 0) {
                    // Save the project and db list
                    projectMap.put(f.getPath(), dbList);
                }
            }

            if (projectMap != null && projectMap.size() > 0) {
                projects.add(new Project(projectMap, file.getPath(), file.getName()));
            }
        }

        // Walk through the items to mark the project
        for (int i = 0; i < projects.size() - 1; i++) {
            Project p1 = projects.get(i);
            for (int j = i + 1; j < projects.size(); j++) {
                Project p2 = projects.get(j);
                p2.checkAndMarkProject(p1);
            }
        }

        // Save the projects
        for (Project project : projects) {
            if (project.getDbListMap() == null) {
                continue;
            }

            // Save to one file
            if (!separateCsv) {
                EAWriter writer = new EAWriter(project.getPath());
                writer.open();

                project.saveToFile(writer, codeForExcel);
                writer.close();
            } else {
                // Save to multiple files
                for (Map.Entry<String, List<EAItem>> dbListEntry : project.getDbListMap().entrySet()) {
                    List<EAItem> dbList = dbListEntry.getValue();
                    if (dbList == null || dbList.size() <= 0) {
                        continue;
                    }

                    EAWriter writer = new EAWriter(dbListEntry.getKey());
                    writer.open();

                    for (EAItem db : dbList) {
                        if (db != null) {
                            db.saveToFile(writer, codeForExcel);
                        }
                    }
                    writer.close();
                }
            }
        }

        System.out.printf("Finished %d folders, %d projects, start: %s, end: %s\n",
                filePaths.size(),
                projects.size(),
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
    }
}
