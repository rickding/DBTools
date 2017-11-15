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
        add(HeaderProcessor.projectNameHeader);
        add(HeaderProcessor.issueKeyHeader);
        add(HeaderProcessor.projectHeader);
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
     * Create data graph based on data "A1:J30" new CellReference(topLeft), new CellReference(botRight)
     * @param graphSheet new CellReference(rowStart, colStart), new CellReference(rowEnd, 24)
     * @param dataSheet
     */
    public XSSFPivotTable createPivotTable(XSSFSheet graphSheet, XSSFSheet dataSheet, Cell topLeft, Cell botRight) {
        if (graphSheet == null || dataSheet == null) {
            return null;
        }

        int[] cellArea = getCellArea(dataSheet);
        if (cellArea == null || cellArea.length < 4) {
            return null;
        }

        int rowStart = cellArea[0];
        int rowEnd = cellArea[1];
        int colStart = cellArea[2];
        int colEnd = cellArea[3];
        if (colEnd > newHeaders.size() - 1) {
            colEnd = newHeaders.size() - 1;
        }

        XSSFPivotTable pivotTable = graphSheet.createPivotTable(new AreaReference(new CellReference(rowStart, colStart), new CellReference(rowEnd, colEnd)), new CellReference("A5"), dataSheet);
        return pivotTable;
    }

    /**
     * configure the pivot table
     * @param pivotTable
     */
    protected void decoratePivotTable(XSSFPivotTable pivotTable) {
        if (pivotTable == null) {
            return;
        }

        // configure the pivot table
        // row label
        pivotTable.addRowLabel(newHeaders.indexOf(HeaderProcessor.projectHeader));
        // col label
        pivotTable.addRowLabel(newHeaders.indexOf(HeaderProcessor.projectNameHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, newHeaders.indexOf(HeaderProcessor.issueKeyHeader));
        // add filter
        pivotTable.addReportFilter(newHeaders.indexOf(HeaderProcessor.releaseDateHeader));
    }
}
