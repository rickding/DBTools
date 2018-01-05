package dbtools.common.file;

import dbtools.common.utils.StrUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileUtils {
    public static String[] read(String filePath) {
        if (StrUtils.isEmpty(filePath)) {
            return null;
        }

        FileReader reader = new FileReader(filePath);
        if (!reader.open()) {
            System.out.printf("Fail to open file: %s\n", filePath);
            return null;
        }

        // read and update, then write
        List<String> lines = new ArrayList<String>();
        String str;
        while ((str = reader.readLine()) != null) {
            lines.add(str);
        }

        // close
        reader.close();

        String[] strs = new String[lines.size()];
        lines.toArray(strs);
        return strs;
    }

    public static String getOutputFileName(File parent, File file, String fileExt, String newFileName, String newFolderName) {
        if (file == null || parent == null) {
            return null;
        }
        return getOutputFileName(parent, file.getName(), fileExt, newFileName, newFolderName);
    }

    public static String getOutputFileName(File parent, String fileName, String fileExt, String newFileName, String newFolderName) {
        if (parent == null) {
            return null;
        }

        String outputFileName = null;
        if (parent.isDirectory()) {
            // Prepare the folder firstly
            String outputFolderName = String.format("%s%s", parent.getPath(), newFolderName);
            File folder = new File(outputFolderName);
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Output file name
            outputFileName = String.format("%s\\%s", outputFolderName, fileName);
        } else {
            outputFileName = parent.getPath();
        }

        // Format the file name
        if (!StrUtils.isEmpty(outputFileName)) {
            if (!StrUtils.isEmpty(fileExt) && outputFileName.toLowerCase().endsWith(fileExt)) {
                outputFileName = outputFileName.substring(0, outputFileName.length() - fileExt.length());
            }
            outputFileName = String.format("%s%s", outputFileName, newFileName);
        }

        return outputFileName;
    }

    public static File[] findFiles(String filePath, String fileExt, String ignoreFilePostFix) {
        return findFiles(filePath, null, fileExt, ignoreFilePostFix);
    }

    public static File[] findFiles(String filePath, final String fileNamePrefix, final String fileExt, final String ignoreFileName) {
        if (StrUtils.isEmpty(filePath)) {
            return null;
        }

        File file = new File(filePath);
        if (!file.exists()) {
            return null;
        }

        // File or directory, while not iterate sub folders
        File[] files = null;
        if (file.isFile() && filePath.toLowerCase().endsWith(fileExt)) {
            files = new File[]{file};
        } else if (file.isDirectory()) {
            files = file.listFiles(new FilenameFilter() {
                // @Override
                public boolean accept(File dir, String name) {
                    if (StrUtils.isEmpty(name)) {
                        return false;
                    }

                    String str = name.toLowerCase();
                    if (!StrUtils.isEmpty(fileNamePrefix) && !str.startsWith(fileNamePrefix.toLowerCase())) {
                        return false;
                    }

                    if (!StrUtils.isEmpty(fileExt) && !str.endsWith(fileExt)) {
                        return false;
                    }
                    return StrUtils.isEmpty(ignoreFileName) || !str.endsWith(ignoreFileName.toLowerCase());
                }
            });
        }

        return files;
    }
}
