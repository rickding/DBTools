package dbtools.common.file;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import dbtools.common.utils.StrUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CsvUtil {
    public static void saveToFile(List<String[]> recordList, String csvFile) {
        if (recordList == null || recordList.size() <= 0 || StrUtils.isEmpty(csvFile)) {
            return;
        }

        CsvWriter writer = new CsvWriter(csvFile, ',', Charset.forName("UTF-8"));
        if (null == writer) {
            System.out.printf("Fail to open file: %s\n", csvFile);
            return;
        }

        try {
            for (String[] record : recordList) {
                writer.writeRecord(record);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }

    /**
     * Return the list of csv values, the first one is header list.
     * @param csvFile
     * @return
     */
    public static List<String[]> readFile(String csvFile) {
        if (StrUtils.isEmpty(csvFile)) {
            return null;
        }

        CsvReader reader = null;
        try {
            reader = new CsvReader(csvFile, ',', Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.printf("Fail to open file: %s\n", csvFile);
        }

        if (reader == null) {
            return null;
        }

        List<String[]> recordList = new ArrayList<String[]>();

        try {
            // headers
            reader.readHeaders();
            String[] headers = reader.getHeaders();
            recordList.add(headers);

            // data
            while (reader.readRecord()) {
                String[] values = reader.getValues();
                recordList.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.printf("Fail to read file: %s\n", csvFile);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.printf("Fail to process file: %s\n", csvFile);
        } finally {
            reader.close();
        }

        return recordList;
    }
}
