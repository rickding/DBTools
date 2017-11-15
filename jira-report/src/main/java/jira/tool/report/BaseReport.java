package jira.tool.report;

import dbtools.common.utils.StrUtils;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.ProjectNameProcessor;
import jira.tool.report.processor.ReleaseDateProcessor;
import jira.tool.report.processor.ValueProcessor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;

public class BaseReport {
    /**
     * Create report instance for special file
     * @param fileName
     * @return
     */
    public static BaseReport getReport(String fileName) {
        if (!StrUtils.isEmpty(fileName)) {
            if (fileName.startsWith("未完成开发")) {
                return new SprintReport();
            } else if (fileName.startsWith("到期日没有或超过4周")) {
                return new NoDueDateReport();
            } else if (fileName.startsWith("完成开发待提测")) {
                return new DevFinishReport();
            }
        }
        return new BaseReport();
    }

    // Configure the processors
    protected List<ValueProcessor> valueProcessors = new ArrayList<ValueProcessor>() {{
        add(new ProjectNameProcessor());
        add(new ReleaseDateProcessor());
    }};

    // Configure the headers
    protected List<HeaderProcessor> newHeaders = new ArrayList<HeaderProcessor>() {{
        add(HeaderProcessor.releaseDateHeader);
    }};

    // Configure the sheet name
    protected Map<String, String> mapSheetName = new HashMap<String, String>() {{
        put("data", "data");
        put("graph", "graph");
    }};

    public String getSheetName(String sheet) {
        return mapSheetName.get(sheet);
    }

    /**
     * Combine the headers
     * @param headers
     * @return
     */
    public HeaderProcessor[] processHeaders(String[] headers) {
        // new ones
        List<HeaderProcessor> allHeaders = new ArrayList<HeaderProcessor>() {{
            addAll(newHeaders);
        }};

        // Old ones
        HeaderProcessor[] tmp = HeaderProcessor.fromStrings(headers);
        allHeaders.addAll(Arrays.asList(tmp));

        // Return the combined ones
        tmp = new HeaderProcessor[allHeaders.size()];
        allHeaders.toArray(tmp);
        return tmp;
    }

    /**
     * Process and return the new value
     * @param header
     * @param value
     * @return
     */
    public String processValue(String header, String value) {
        if (valueProcessors == null || valueProcessors.size() <= 0) {
            return value;
        }

        // Call the processors
        for (ValueProcessor valueProcessor : valueProcessors) {
            if (valueProcessor.accept(header)) {
                value = valueProcessor.process(value);
            }
        }
        return value;
    }

    /**
     * Add the filter and lock
     * @param sheet
     */
    public void decorateDataSheet(XSSFSheet sheet) {
        if (sheet == null) {
            return;
        }

        int[] cellArea = getCellArea(sheet);
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

    /**
     * Return cell area: row start, row end, col start, col end
     * @param sheet
     * @return
     */
    public int[] getCellArea(XSSFSheet sheet) {
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

    /**
     * Create data graph based on data
     * @param graphSheet
     * @param dataSheet
     */
    public XSSFPivotTable createPivotTable(XSSFSheet graphSheet, XSSFSheet dataSheet, Cell topLeft, Cell botRight) {
        if (graphSheet == null || dataSheet == null) {
            return null;
        }
        XSSFPivotTable pivotTable = graphSheet.createPivotTable(new AreaReference("A1:J30"), new CellReference("A5"), dataSheet);
        return pivotTable;
    }

    protected void decoratePivotTable(XSSFPivotTable pivotTable) {
        if (pivotTable == null) {
            return;
        }

        //Configure the pivot table
        //Use first column as row label
        pivotTable.addRowLabel(0);
        //Sum up the second column
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, 1);
        //Set the third column as filter
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, 2);
        //Add filter on forth column
        pivotTable.addReportFilter(3);
    }
}
