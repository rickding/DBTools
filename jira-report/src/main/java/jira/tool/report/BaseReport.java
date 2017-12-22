package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.model.Story;
import jira.tool.report.processor.EstimationProcessor;
import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.SprintDateProcessor;
import jira.tool.report.processor.StartDateProcessor;
import jira.tool.report.processor.TeamNameProcessor;
import jira.tool.report.processor.ValueProcessor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataConsolidateFunction;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFPivotTable;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseReport {
    // Configure the processors
    protected List<ValueProcessor> valueProcessors = new ArrayList<ValueProcessor>() {{
        add(new SprintDateProcessor());
        add(new StartDateProcessor());
        add(new TeamNameProcessor());
        add(new EstimationProcessor());
    }};

    // Configure the sheet name
    protected Map<String, String> mapSheetName = new HashMap<String, String>() {{
        put("data", "data");
        put("graph", "graph");
    }};

    protected String duration = null;
    protected String dateStr = DateUtils.format(new Date(), "MMdd");

    /**
     * return the template file
     *
     * @return
     */
    public String getTemplateName() {
        return null; // "template.xlsx";
    }

    public String getFileName() {
        return String.format("BaseReport%s.xlsx", dateStr);
    }

    // Read story list from db
    protected List<Story> getStoryList() {
        return null;
    }

    // Post the graph data to rms
    protected void postToRms(List<Map<String, String>> records) {
    }

    public String getName() {
        String name = getFileName();
        if (!StrUtils.isEmpty(name)) {
            for (String sep : new String[]{"-", dateStr}) {
                if (name.indexOf(sep) > 0) {
                    name = name.trim().substring(0, name.indexOf(sep));
                    break;
                }
            }
        }
        if (StrUtils.isEmpty(name)) {
            name = this.getClass().getName();
        }
        return name;
    }

    /**
     * Fill data sheets
     *
     * @return
     */
    public XSSFSheet[] fillDataSheets(XSSFWorkbook wb) {
        // Subclass will override to add the additional data sheet
        if (wb == null) {
            return null;
        }

        XSSFSheet dataSheet = ExcelUtil.getOrCreateSheet(wb, getSheetName("data"));
        JiraUtilEx.fillSheetFromDB(dataSheet, getStoryList(), this);

        if (!isTemplateUsed()) {
            decorateDataSheet(dataSheet);

            // Pivot table
            XSSFSheet graphSheet = ExcelUtil.getOrCreateSheet(wb, getSheetName("graph"));
            XSSFPivotTable pivotTable = createPivotTable(graphSheet, dataSheet, HeaderProcessor.headerList.size() - 1);
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

    public XSSFWorkbook getWorkbook(String filePath) {
        String templateName = getTemplateName();
        XSSFWorkbook wb = null;
        if (StrUtils.isEmpty(templateName) || StrUtils.isEmpty(filePath)) {
            wb = new XSSFWorkbook();
        } else {
            // Open the template
            templateName = String.format("%s\\%s", filePath, templateName);
            try {
                wb = new XSSFWorkbook(templateName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return wb;
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
            addAll(HeaderProcessor.headerList);
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
    protected String processValue(String header, String value, Cell cell) {
        if (valueProcessors == null || valueProcessors.size() <= 0 || cell == null) {
            return value;
        }

        // Call the processors
        for (ValueProcessor valueProcessor : valueProcessors) {
            if (valueProcessor.accept(header)) {
                value = valueProcessor.process(value, cell);
                return value;
            }
        }

        // Set directly
        cell.setCellValue(value);
        return value;
    }

    /**
     * Add the filter and lock
     *
     * @param sheet
     */
    protected void decorateDataSheet(XSSFSheet sheet) {
        ExcelUtil.filterAndLockSheet(sheet);
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
        pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.projectHeader));
        // col label
        pivotTable.addRowLabel(HeaderProcessor.headerList.indexOf(HeaderProcessor.teamNameHeader));
        // sum up
        pivotTable.addColumnLabel(DataConsolidateFunction.COUNT, HeaderProcessor.headerList.indexOf(HeaderProcessor.issueKeyHeader));
        // add filter
        pivotTable.addReportFilter(HeaderProcessor.headerList.indexOf(HeaderProcessor.dueDateHeader));
    }
}
