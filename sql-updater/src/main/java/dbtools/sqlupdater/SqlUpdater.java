package dbtools.sqlupdater;

import java.io.File;

/**
 * Created by user on 2017/9/30.
 */
public class SqlUpdater {
    public static String File_Ext = ".sql";

    public static void processFile(File file) {
        if (file == null || !file.canRead()) {
            return;
        }
    }
}
