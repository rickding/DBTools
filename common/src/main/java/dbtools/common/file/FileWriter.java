package dbtools.common.file;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

/**
 * Created by user on 2017/10/1.
 */
public class FileWriter {
    private String filePath;
    private BufferedWriter writer;

    public FileWriter(String filePath) {
        if (StrUtils.isEmpty(filePath)) {
            filePath = "fw";
        }
        this.filePath = filePath;
    }

    public boolean isOpen() {
        return writer != null;
    }

    public boolean open() {
        if (isOpen()) {
            close();
        }

        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath), "UTF-8"));
        } catch (IOException e) {
            writer = null;
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        boolean isOpen = isOpen();
        System.out.printf("FileWriter open %s: %s\n", isOpen ? "successfully" : "Failed", filePath);
        return isOpen;
    }

    public void close() {
        if (!isOpen()) {
            return;
        }

        try {
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            writer = null;
        }
        System.out.printf("FileWriter close successfully: %s\n", filePath);
    }

    public void writeLine(List<String> strList) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly.");
            return;
        }

        if (strList == null || strList.size() <= 0) {
            return;
        }

        for (String str : strList) {
            writeLine(str);
        }
    }

    public void writeLines(List<String[]> strs) {
        if (strs == null || strs.size() <= 0) {
            return;
        }
        for (String[] str : strs) {
            writeLines(str);
        }
    }

    public void writeLines(String[] strs) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly.");
            return;
        }

        if (ArrayUtils.isEmpty(strs)) {
            return;
        }

        for (String str : strs) {
            writeLine(str);
        }
    }

    public void writeLine(String str) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly.");
            return;
        }

        write(str);

        try {
            writer.write("\r\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void write(String str) {
        if (!isOpen()) {
            System.out.println("Please call open() firstly.");
            return;
        }

        if (StrUtils.isEmpty(str)) {
            return;
        }

        try {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}
