package dbtools.common.file;

import dbtools.common.utils.StrUtils;

import java.io.File;
import java.util.Map;

public class FileUpdater {
    public static void processFile(File inputFile, String outputFileName, Map<String, String> replaceMap, Map<String, String> restoreMap,
                                   long headerLineIndex, String[] headerArray, String tailFlag) {
        if (inputFile == null || !inputFile.exists() || !inputFile.canRead() || StrUtils.isEmpty(outputFileName)) {
            return;
        }

        // Input
        String filePath = inputFile.getPath();
        FileReader reader = new FileReader(filePath);
        if (!reader.open()) {
            System.out.printf("Fail to open file: %s\n", filePath);
            return;
        }

        // Output
        FileWriter writer = new FileWriter(outputFileName);
        if (!writer.open()) {
            System.out.printf("Fail to create output file: %s\n", outputFileName);

            // Close
            reader.close();
            return;
        }

        // Mark the header written or not
        long index = 0;
        boolean headerWritten = headerLineIndex < 0;
        boolean tailFound = false;

        // read and update, then write
        String str;
        while ((str = reader.readLine()) != null) {
            if (!StrUtils.isEmpty(str)) {
                // Check if it's the tail
                if (!tailFound && isTail(str, tailFlag)) {
                    tailFound = true;
                    System.out.printf("Tail is found: %s\n", outputFileName);
                }

                // Update the str;
                str = updateString(str, replaceMap, restoreMap);
            }

            // Write header
            if (!headerWritten && headerLineIndex <= index++) {
                headerWritten = true;
                index += writeHeaders(writer, headerArray);
                System.out.printf("Header is written: %d, %s\n", index, outputFileName);
            }

            // Write line
            writer.writeLine(str);
        }

        // close
        writer.close();
        reader.close();
    }

    private static boolean isTail(String str, String tailFlag) {
        if (StrUtils.isEmpty(str) || StrUtils.isEmpty(tailFlag)) {
            return false;
        }

        return str.toLowerCase().startsWith(tailFlag);
    }

    private static String updateString(String fileString, Map<String, String> replaceMap, Map<String, String> restoreMap) {
        if (StrUtils.isEmpty(fileString)) {
            return fileString;
        }

        // Replace
        for (Map.Entry<String, String> replace : replaceMap.entrySet()) {
            String str = replace.getKey();
            if (StrUtils.isEmpty(str)) {
                continue;
            }
            fileString = fileString.replace(str, replace.getValue());
        }

        // Restore after replace
        for (Map.Entry<String, String> replace : restoreMap.entrySet()) {
            String str = replace.getKey();
            if (StrUtils.isEmpty(str)) {
                continue;
            }
            fileString = fileString.replace(str, replace.getValue());
        }
        return fileString;
    }

    private static long writeHeaders(FileWriter writer, String[] updateHeaderArray) {
        if (writer == null || !writer.isOpen()) {
            return 0;
        }

        for (String str : updateHeaderArray) {
            writer.writeLine(str);
        }
        return updateHeaderArray.length;
    }
}
