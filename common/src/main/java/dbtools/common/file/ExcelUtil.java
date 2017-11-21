package dbtools.common.file;

import com.csvreader.CsvReader;
import dbtools.common.utils.StrUtils;
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

    public static XSSFSheet getOrCreateSheet(XSSFWorkbook wb, String sheetName) {
        if (wb == null) {
            return null;
        }

        XSSFSheet sheet = null;
        if (!StrUtils.isEmpty(sheetName)) {
            sheet = wb.getSheet(sheetName);
            if (sheet == null) {
                sheet = wb.createSheet(sheetName);
            }
        } else {
            sheet = wb.createSheet();
        }
        return sheet;
    }

    /**
     * Return cell area: row start, row end, col start, col end
     *
     * @param sheet
     * @return
     */
    public static int[] getCellArea(XSSFSheet sheet) {
        if (sheet == null) {
            return null;
        }

        int rowStart = sheet.getFirstRowNum();
        int rowEnd = sheet.getLastRowNum();
        int colStart = 0;
        int colEnd = 0;

        while (true) {
            Row row = sheet.getRow(rowStart);
            if (row != null) {
                colStart = row.getFirstCellNum();
                colEnd = row.getLastCellNum();
                break;
            }
        }

        return new int[]{rowStart, rowEnd, colStart, colEnd};
    }

    public static String[] getRowValues(XSSFSheet sheet, int row) {
        if (sheet == null || row < 0) {
            return null;
        }

        Row r = sheet.createRow(row);
        if (r == null) {
            return null;
        }

        int colStart = r.getFirstCellNum();
        int colEnd = r.getLastCellNum();
        if (colStart < 0 || colStart > colEnd) {
            return null;
        }

        String[] values = new String[colEnd - colStart + 1];
        for (int j = colStart; j < colEnd; j++) {
            Cell cell = r.createCell(j);
            values[j - colStart] = cell == null ? null : cell.getStringCellValue();
        }

        return values;
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

        Row r = sheet.createRow(row);
        if (r == null) {
            return null;
        }

        Cell left = null, right = null;
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
    public final static Cell[] fillSheetFromCsv(XSSFSheet sheet, String csvFile) {
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
            String[] headers = reader.getHeaders();

            // Save the headers
            int row = 0;
            Cell[] cells = fillRow(sheet, row++, headers);
            topLeft = cells == null || cells.length < 1 ? null : cells[0];

            // Save the data
            while (reader.readRecord()) {
                // New row
                Row r = sheet.createRow(row++);

                int col = 0;
                for (String header : headers) {
                    Cell cell = r.createCell(col++);

                    // Read and convert the value
                    String v = reader.get(header);

                    // Save value to cell directly
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
