package jira.tool.report;

import dbtools.common.utils.StrUtils;
import jira.tool.report.processor.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.util.*;

public class BaseReport {
    public static Map<String, BaseReport> reportMap = new HashMap<String, BaseReport>() {{
        put("到期日没有或超过4周", new NoDueDateReport());
        put("完成开发待提测", new DevFinishReport());
        put("未完成开发", new ReleasePlanReport());
        put("计划交付", new WeeklyReleasePlanReport());
        put("计划开始", new StartPlanReport());
    }};

    /**
     * Create report instance for special file
     *
     * @param fileName
     * @return
     */
    public static BaseReport getReport(String fileName) {
        if (!StrUtils.isEmpty(fileName)) {
            for (Map.Entry<String, BaseReport> report : reportMap.entrySet()) {
                if (fileName.startsWith(report.getKey())) {
                    return report.getValue();
                }
            }
        }
        return new BaseReport();
    }

    // Configure the processors
    protected List<ValueProcessor> valueProcessors = new ArrayList<ValueProcessor>() {{
        add(new TeamNameProcessor());
        add(new ReleaseDateProcessor());
        add(new TimeProcessor());
    }};

    // Configure the headers
    protected List<HeaderProcessor> newHeaders = new ArrayList<HeaderProcessor>() {{
        add(HeaderProcessor.dueDateHeader);
        add(HeaderProcessor.teamKeyHeader);
        add(HeaderProcessor.teamNameHeader);
        add(HeaderProcessor.issueKeyHeader);
        add(HeaderProcessor.projectHeader);
        add(HeaderProcessor.timeHeader);
    }};

    // Configure the sheet name
    protected Map<String, String> mapSheetName = new HashMap<String, String>() {{
        put("data", "data");
        put("graph", "graph");
    }};

    /**
     * Fill data sheets
     *
     * @return
     */
    public XSSFSheet[] fillDataSheets(XSSFWorkbook wb) {
        // Subclass implements the additional data sheet
        return null;
    }

    /**
     * Fill data sheets from csv files
     */
    public XSSFSheet[] fillDataSheets(XSSFWorkbook wb, String[] csvFiles) {
        if (csvFiles == null || csvFiles.length <= 0 || wb == null) {
            return null;
        }

        XSSFSheet dataSheet = ExcelUtil.getOrCreateSheet(wb, getSheetName("data"));

        // Base data from csv file
        ExcelUtil.fillSheetFromCsv(dataSheet, csvFiles[0], this);

        if (!isTemplateUsed()) {
            decorateDataSheet(dataSheet);

            // Pivot table
            XSSFSheet graphSheet = ExcelUtil.getOrCreateSheet(wb, getSheetName("graph"));
            XSSFPivotTable pivotTable = createPivotTable(graphSheet, dataSheet, newHeaders.size() - 1);
            if (pivotTable != null) {
                decoratePivotTable(pivotTable);
            } else {
                wb.removeSheetAt(wb.getSheetIndex(graphSheet));
            }
        }
        return new XSSFSheet[]{dataSheet};
    }

    /**
     * return the specified sheet name
     *
     * @param sheet
     * @return
     */
    protected String getSheetName(String sheet) {
        return mapSheetName.get(sheet);
    }

    /**
     * return the template file
     * @return
     */
    public String getTemplateName() {
        return null; // "template.xlsx";
    }

    public boolean isTemplateUsed() {
        return !StrUtils.isEmpty(getTemplateName());
    }

    /**
     * Combine the headers
     *
     * @param headers
     * @return
     */
    protected HeaderProcessor[] processHeaders(String[] headers) {
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
     *
     * @param header
     * @param value
     * @return
     */
    protected void processValue(String header, String value, Cell cell) {
        if (valueProcessors == null || valueProcessors.size() <= 0 || cell == null) {
            return;
        }

        // Call the processors
        for (ValueProcessor valueProcessor : valueProcessors) {
            if (valueProcessor.accept(header)) {
                valueProcessor.process(value, cell);
                return;
            }
        }

        // Set directly
        cell.setCellValue(value);
    }

    /**
     * Add the filter and lock
     *
     * @param sheet
     */
    protected void decorateDataSheet(XSSFSheet sheet) {
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

    /**
     * Create data graph based on data "A1:J30" new CellReference(topLeft), new CellReference(botRight)
     *
     * @param graphSheet new CellReference(rowStart, colStart), new CellReference(rowEnd, 24)
     * @param dataSheet
     */
    protected XSSFPivotTable createPivotTable(XSSFSheet graphSheet, XSSFSheet dataSheet, int maxColIndex) {
        if (graphSheet == null || dataSheet == null) {
            return null;
        }

        int[] cellArea = ExcelUtil.getCellArea(dataSheet);
        if (cellArea == null || cellArea.length < 4) {
            return null;
        }

        int rowStart = cellArea[0];
        int rowEnd = cellArea[1];
        int colStart = cellArea[2];
        int colEnd = cellArea[3] - 1;
        if (colEnd > maxColIndex) {
            colEnd = maxColIndex;
        }

        XSSFPivotTable pivotTable = graphSheet.createPivotTable(new AreaReference(new CellReference(rowStart, colStart), new CellReference(rowEnd, colEnd)), new CellReference("A5"), dataSheet);
        return pivotTable;
    }

    /**
     * configure the pivot table
     *
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
        pivotTable.addRowLabel(newHeaders.indexOf(HeaderProcessor.teamNameHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, newHeaders.indexOf(HeaderProcessor.issueKeyHeader));
        // add filter
        pivotTable.addReportFilter(newHeaders.indexOf(HeaderProcessor.dueDateHeader));
    }
}
