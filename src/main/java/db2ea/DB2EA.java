package db2ea;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

/**
 * Created by user on 2017/9/23.
 */
public class DB2EA {
    public static void main(String[] args) {
        if (ArrayUtils.isEmpty(args)) {
            System.out.println("Please specify the MySQL dumped file or folder to convert multiple ones!");
            return;
        }

        Date time_start = new Date();

        // Read files
        for (String arg : args) {
            if (StrUtils.isEmpty(arg)) {
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
                    @Override
                    public boolean accept(File dir, String name) {
                        return !StrUtils.isEmpty(name) && name.toLowerCase().endsWith(SqlParser.File_SQL_Ext);
                    }
                });
            }

            if (files == null || files.length <= 0) {
                continue;
            }

            EAWriter writer = new EAWriter(file.getPath());
            writer.open();

            for (File f : files) {
                SqlParser.processFile(f, writer);
            }
            writer.close();
        }

        System.out.printf("Finished, start: %s, end: %s\n",
                DateUtils.format(time_start, "hh:mm:ss"),
                DateUtils.format(new Date(), "hh:mm:ss")
        );
    }
}
