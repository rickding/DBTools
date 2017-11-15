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
    /**
     * http://poi.apache.org/spreadsheet/quick-guide.html
     * Save excel file
     * @param wb
     * @param filename
     */
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

    /**
     * Fill cells with value
     * @param sheet
     * @param row
     * @param col
     * @param value
     */
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

    /**
     * @param sheet
     * @param row
     * @param values
     * @return return the first left and the last right cells
     */
    public static Cell[] fillRow(XSSFSheet sheet, int row, String[] values) {
        if (sheet == null || row < 0 || values == null || values.length <= 0) {
            return null;
        }

        Cell left = null, right = null;
        Row r = sheet.createRow(row);
        for (int j = 0; j < values.length; j++) {
            Cell cell = r.createCell(j);
            cell.setCellValue(values[j]);

            if (left == null) {
                left = cell;
            }
            right = cell;
        }

        return new Cell[]{left, right};
    }

    /**
     * transform csv file into excel file
     *
     * @param sheet   name of a excel file that will store the transformed file
     * @param csvFile name of a csv file that will be transformed
     */
    public final static Cell[] csvToExcel(XSSFSheet sheet, String csvFile, BaseReport report) {
        if (sheet == null || StrUtils.isEmpty(csvFile)) {
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

        Cell topLeft = null;
        Cell botRight = null;

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
            Cell[] cells = fillRow(sheet, row++, HeaderProcessor.toStrings(headers));
            topLeft = cells == null || cells.length < 1 ? null : cells[0];

            // Save the data
            while (reader.readRecord()) {
                // New row
                Row r = sheet.createRow(row++);

                int col = 0;
                for (HeaderProcessor header : headers) {
                    // Read and convert the value
                    String v = reader.get(header.getValue());
                    if (!StrUtils.isEmpty(v) && report != null) {
                        v = report.processValue(header.getName(), v);
                    }

                    // Save value to cell
                    Cell cell = r.createCell(col++);
                    cell.setCellValue(v);
                    botRight = cell;
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

        return new Cell[] {topLeft, botRight};
    }
}
