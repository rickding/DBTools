package db2ea;

import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by user on 2017/9/23.
 */
public class EAWriter {
    private String fileName;
    private FileWriter fileWriter;

    public EAWriter(String fileName) {
        if (!StrUtils.isEmpty(fileName) && fileName.toLowerCase().endsWith(SqlParser.File_SQL_Ext)) {
            fileName = fileName.substring(0, fileName.length() - SqlParser.File_SQL_Ext.length());
        }
        this.fileName = String.format("%s%s", fileName == null ? "file" : fileName, SqlParser.File_EA_Ext);
    }

    public void open() {
        if (fileWriter != null) {
            close();
        }

        try {
            fileWriter = new FileWriter(fileName);
            fileWriter.write("Name,Type,Stereotype,CSV_KEY,CSV_PARENT_KEY\n");
        } catch (IOException e) {
            fileWriter = null;
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void close() {
        if (fileWriter == null) {
            return;
        }

        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            fileWriter = null;
        }
    }

    public void write(EAItem item) {
        if (fileWriter == null) {
            System.out.println("Please call open() firstly.");
            return;
        }

        try {
            fileWriter.write(item.toString());
            fileWriter.write("\n");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }
}