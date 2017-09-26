package db2ea;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by user on 2017/9/23.
 */
public class EAWriter {
    private String fileName;
    private BufferedWriter fileWriter;
    private EAItem pack;
    private IdChecker idChecker;

    public static String Field_Marker = "\"";
    public static String Field_Marker_Replace = "'";
    public static String Field_Separator = ",";
    public static String Field_Header = "Alias,Name,Type,Stereotype,Phase,CSV_KEY,CSV_PARENT_KEY";

    public EAWriter(String fileName) {
        if (!StrUtils.isEmpty(fileName) && fileName.toLowerCase().endsWith(SqlParser.File_SQL_Ext)) {
            fileName = fileName.substring(0, fileName.length() - SqlParser.File_SQL_Ext.length());
        }
        this.fileName = String.format("%s%s", fileName == null ? "file" : fileName, SqlParser.File_EA_Ext);
    }

    public boolean isOpen() {
        return fileWriter != null;
    }

    public EAItem getPack() {
        return pack;
    }

    /**
     * open, write header, create root package which will be the first level elements' parent
     * @return
     */
    public EAItem open() {
        if (fileWriter != null) {
            close();
        }

        pack = null;
        idChecker = new IdChecker();

        try {
            fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));

            // headers
            fileWriter.write(Field_Header);
            fileWriter.newLine();

            // package
            // pack = new EAItem("EA包", EAType.Package, EAStereotype.None, null);
            write(pack);
        } catch (IOException e) {
            fileWriter = null;
            e.printStackTrace();
            System.out.println(e.getMessage());
        }

        return pack;
    }

    public void close() {
        if (fileWriter == null) {
            return;
        }

        pack = null;
        idChecker = null;

        try {
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        } finally {
            fileWriter = null;
        }
        System.out.printf("EAWrite saves successfully: %s\n", fileName);
    }

    public void write(EAItem item) {
        if (item == null) {
            return;
        }

        if (idChecker != null && idChecker.isDuplicated(item.getId())) {
            System.out.printf("Duplicated id: %s, %s\n", item.getId(), item.toString());
            return;
        }

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