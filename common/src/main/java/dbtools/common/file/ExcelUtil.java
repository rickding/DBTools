package dbtools.common.file;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

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

    public static void fillSheet(XSSFSheet sheet, List<String[]> recordList) {
        if (recordList == null || recordList.size() <= 0 || sheet == null) {
            return;
        }

        int row = 0;
        for (String[] record : recordList) {
            fillRow(sheet, row++, record);
        }

        filterAndLockSheet(sheet);
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
     * Add the filter and lock
     *
     * @param sheet
     */
    public static void filterAndLockSheet(XSSFSheet sheet) {
        if (sheet == null) {
            return;
        }

        int[] cellArea = ExcelUtil.getCellArea(sheet);
        if (cellArea == null || cellArea.length < 4) {
            return;
        }

        int rowStart = cellArea[0];
        int rowEnd = cellArea[1];
        int colStart = cellArea[2];
        int colEnd = cellArea[3];

        // Add the filter
        sheet.setAutoFilter(new CellRangeAddress(rowStart, rowEnd, colStart, colEnd));

        // Set the free panes, the first row
        sheet.createFreezePane(0, 1, colStart, rowStart + 1);
    }
}
