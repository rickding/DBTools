package jira.tool.report;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelSample {
    public static void excelSample() {
        // http://poi.apache.org/spreadsheet/quick-guide.html
        XSSFWorkbook[] wbs = new XSSFWorkbook[]{
                new XSSFWorkbook(),
        };

        for (int i = 0; i < wbs.length; i++) {
            XSSFWorkbook wb = wbs[i];
            CreationHelper createHelper = wb.getCreationHelper();

            // create a new sheet
            XSSFSheet s = wb.createSheet();
            // declare a row object reference
            // declare a cell object reference
            // create 2 cell styles
            CellStyle cs = wb.createCellStyle();
            CellStyle cs2 = wb.createCellStyle();
            DataFormat df = wb.createDataFormat();

            // create 2 fonts objects
            Font f = wb.createFont();
            Font f2 = wb.createFont();

            // Set font 1 to 12 point type, blue and bold
            f.setFontHeightInPoints((short) 12);
            f.setColor(IndexedColors.RED.getIndex());
            f.setBoldweight(Font.BOLDWEIGHT_BOLD);

            // Set font 2 to 10 point type, red and bold
            f2.setFontHeightInPoints((short) 10);
            f2.setColor(IndexedColors.RED.getIndex());
            f2.setBoldweight(Font.BOLDWEIGHT_BOLD);

            // Set cell style and formatting
            cs.setFont(f);
            cs.setDataFormat(df.getFormat("#,##0.0"));

            // Set the other cell style and formatting
            cs2.setBorderBottom(cs2.BORDER_THIN);
            cs2.setDataFormat(df.getFormat("text"));
            cs2.setFont(f2);

            Cell topLeft = null, botRight = null;

            Row r = s.createRow(0);
            for (int cellnum = 0; cellnum < 10; cellnum += 2) {
                Cell c = r.createCell(cellnum);
                Cell c2 = r.createCell(cellnum + 1);

                c.setCellValue("标题" + cellnum);
                c2.setCellValue("测试汉纸" + (cellnum + 1));

                if (topLeft == null) {
                    topLeft = c;
                }
            }

            // Define a few rows
            for (int rownum = 1; rownum < 30; rownum++) {
                r = s.createRow(rownum);
                for (int cellnum = 0; cellnum < 10; cellnum += 2) {
                    Cell c = r.createCell(cellnum);
                    Cell c2 = r.createCell(cellnum + 1);

                    c.setCellValue((double) rownum + (cellnum / 10));
                    c2.setCellValue(createHelper.createRichTextString("测试汉纸! " + cellnum));
                    botRight = c2;
                }
            }

            // PivotTable "A1:J30"
            XSSFSheet s2 = wb.createSheet();
//            ExcelUtil.fillSheet(s2, 10, 10, null);
            XSSFPivotTable pivotTable = s2.createPivotTable(new AreaReference(new CellReference(topLeft), new CellReference(botRight)), new CellReference("A5"), s);
            //Configure the pivot table
            //Use first column as row label
            pivotTable.addRowLabel(0);
            //Sum up the second column
            pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, 1);
            //Set the third column as filter
            pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, 2);
            //Add filter on forth column
            pivotTable.addReportFilter(3);

            // Save
            String filename = "workbook.xls";
            if (wb instanceof XSSFWorkbook) {
                filename = filename + "x";
            }

            ExcelUtil.saveToFile(wb, filename);
        }
    }
}
