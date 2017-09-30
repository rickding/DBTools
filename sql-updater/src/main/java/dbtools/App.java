package dbtools;

import dbtools.common.utils.DateUtils;

import java.util.*;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Specify the MySQL dumped file or folder to convert:");
        System.out.println("optional: -code-for-excel: specify to generate file for excel to parse.");
        System.out.println("optional: -separate-csv: specify to generate one csv file or multiple ones.");
        System.out.println("folder or *.sql file: one or multiple ones, to specify the folder or sql file to parse.");

        Date time_start = new Date();
        Set<String> filePaths = new HashSet<String>(){{
        }};
        List<String> projects = new ArrayList<String>();

        System.out.printf("Finished %d folders, %d files, start: %s, end: %s\n",
                filePaths.size(),
                projects.size(),
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
    }
}
