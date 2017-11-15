package jira.tool.report;

import com.csvreader.CsvReader;
import dbtools.common.utils.StrUtils;
import jira.tool.report.processor.HeaderProcessor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;

public class ExcelUtil {
    public static void saveToFile(XSSFWorkbook wb, String filename) {
        if (wb == null || StrUtils.isEmpty(filename)) {
            return;
        }

        try {
            FileOutputStream out = new FileOutputStream(filename);
            wb.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void fillSheet(XSSFSheet sheet, int row, int col, String value) {
        if (sheet == null || row <= 0 || col <= 0) {
            return;
        }

        for (int i = 0; i < row; i++) {
            Row r = sheet.createRow(i);
            for (int j = 0; j < col; j++) {
                Cell cell = r.createCell(j);
                if (value != null) {
                    cell.setCellValue(value);
                }
            }
        }
    }

    public static void fillRow(XSSFSheet sheet, int row, String[] values) {
        if (sheet == null || row < 0 || values == null || values.length <= 0) {
            return;
        }

        Row r = sheet.createRow(row);
        for (int j = 0; j < values.length; j++) {
            Cell cell = r.createCell(j);
            cell.setCellValue(values[j]);
        }
    }

    /**
     * transform csv file into excel file
     *
     * @param sheet   name of a excel file that will store the transformed file
     * @param csvFile name of a csv file that will be transformed
     */
    public final static void csvToExcel(XSSFSheet sheet, String csvFile, BaseReport report) {
        if (sheet == null || StrUtils.isEmpty(csvFile)) {
            return;
        }

        CsvReader reader = null;
        try {
            reader = new CsvReader(csvFile, ',', Charset.forName("UTF-8"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.printf("Fail to open file: %s\n", csvFile);
        }

        if (reader == null) {
            return;
        }

        try {
            // Read and add new headers
            reader.readHeaders();
            String[] strHeaders = reader.getHeaders();

            HeaderProcessor[] headers = null;
            if (report != null) {
                headers = report.processHeaders(strHeaders);
            } else {
                headers = HeaderProcessor.fromStrings(strHeaders);
            }

            // Save the headers
            int row = 0;
            fillRow(sheet, row++, HeaderProcessor.toStrings(headers));

            while (reader.readRecord()) {
                // New row
                Row r = sheet.createRow(row++);

                int col = 0;
                for (HeaderProcessor header : headers) {
                    // Read and convert the value
                    String v = reader.get(header.getValue());
                    if (!StrUtils.isEmpty(v)) {
                        v = report.processValue(header.getName(), v);
                    }

                    // Save value to cell
                    Cell cell = r.createCell(col++);
                    cell.setCellValue(v);
                }
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
    }
}
