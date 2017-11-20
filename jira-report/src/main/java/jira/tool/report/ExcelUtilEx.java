package jira.tool.report;

import com.csvreader.CsvReader;
import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.StrUtils;
import jira.tool.report.processor.HeaderProcessor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

public class ExcelUtilEx {
    /**
     * transform csv file into excel file
     *
     * @param sheet   name of a excel file that will store the transformed file
     * @param csvFile name of a csv file that will be transformed
     */
    public final static Cell[] fillSheetFromCsv(XSSFSheet sheet, String csvFile, BaseReport report) {
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
            Cell[] cells = ExcelUtil.fillRow(sheet, row++, HeaderProcessor.toStrings(headers));
            topLeft = cells == null || cells.length < 1 ? null : cells[0];

            // Save the data
            while (reader.readRecord()) {
                // New row
                Row r = sheet.createRow(row++);

                int col = 0;
                for (HeaderProcessor header : headers) {
                    Cell cell = r.createCell(col++);

                    // Read and convert the value
                    String v = reader.get(header.getValue());
                    if (report != null) {
                        report.processValue(header.getName(), v, cell);
                    } else {
                        // Save value to cell directly
                        cell.setCellValue(v);
                    }
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
