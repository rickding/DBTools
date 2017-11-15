package jira.tool.report.processor;

import jira.tool.report.processor.HeaderProcessor;
import jira.tool.report.processor.ProjectNameProcessor;
import jira.tool.report.processor.ReleaseDateProcessor;
import jira.tool.report.processor.ValueProcessor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.*;

public class BaseReport {
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
    public void processSheet(XSSFSheet sheet) {
        if (sheet == null) {
            return;
        }

        // Add the filter
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
        sheet.setAutoFilter(new CellRangeAddress(rowStart, rowEnd, colStart, colEnd));

        // Set the free panes, the first row
        sheet.createFreezePane(0, 1, colStart, rowStart + 1);
    }
}
