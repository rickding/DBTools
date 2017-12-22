package jira.tool.report;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import jira.tool.db.model.Story;
import jira.tool.report.processor.HeaderProcessor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraUtilEx {
    public static void fillSheetFromDB(XSSFSheet sheet, List<Story> storyList, BaseReport report) {
        if (sheet == null || storyList == null || storyList.size() <= 0) {
            return;
        }

        String[] strHeaders = getHeaders();
        if (ArrayUtils.isEmpty(strHeaders)) {
            return;
        }

        HeaderProcessor[] headers = null;
        if (report != null) {
            headers = report.processHeaders(strHeaders);
        } else {
            headers = HeaderProcessor.fromStrings(strHeaders);
        }

        // Save the headers
        int row = 0;
        ExcelUtil.fillRow(sheet, row++, HeaderProcessor.toStrings(headers));

        // Remember the values to post to rms
        List<Map<String, String>> records = new ArrayList<Map<String, String>>();

        // Save the data
        for (Story story : storyList) {
            Map<String, String> values = formatStory(story);
            Map<String, String> nameValueMap = new HashMap<String, String>();

            // Write to excel
            Row r = sheet.createRow(row++);
            int col = 0;
            for (HeaderProcessor header : headers) {
                Cell cell = r.createCell(col++);
                String v = values.get(header.getValue());
                if (report != null) {
                    v = report.processValue(header.getName(), v, cell);
                } else {
                    // Save value to cell directly
                    cell.setCellValue(v);
                }
                nameValueMap.put(header.getName(), v);
            }
            records.add(nameValueMap);
        }

        // Post to rms
        RMSUtil.postReport(String.format("%s_%s", report.getName(), report.getSheetName("data")), report.getDateStr(), report.getDuration(), records);
    }

    private static Map<String, String> formatStory(final Story story) {
        if (story == null) {
            return null;
        }

        return new HashMap<String, String>() {{
            put(HeaderProcessor.dueDateHeader.getValue(), DateUtils.format(story.getDueDate()));
            put(HeaderProcessor.resolveDateHeader.getValue(), DateUtils.format(story.getResolveDate()));
            put(HeaderProcessor.releaseDateHeader.getValue(), DateUtils.format(story.getReleaseDate()));
            put(HeaderProcessor.startDateHeader.getValue(), DateUtils.format(story.getStartDate()));
            put(HeaderProcessor.teamKeyHeader.getValue(), story.getProjectKey());
            put(HeaderProcessor.teamNameHeader.getValue(), story.getProjectName());
            put(HeaderProcessor.issueKeyHeader.getValue(), story.getKey());
            put(HeaderProcessor.issueIdHeader.getValue(), String.valueOf(story.getId()));
            put(HeaderProcessor.issueTypeHeader.getValue(), story.getType());
            put(HeaderProcessor.statusHeader.getValue(), story.getStatus());
            put(HeaderProcessor.resolutionHeader.getValue(), story.getResolution());
            put(HeaderProcessor.projectHeader.getValue(), story.getCustomer());
            put(HeaderProcessor.estimationHeader.getValue(), String.valueOf(story.getEstimation()));
        }};
    }

    private static String[] getHeaders() {
        String[] headers = new String[HeaderProcessor.headerList.size()];
        for (int i = 0; i < HeaderProcessor.headerList.size(); i++) {
            headers[i] = HeaderProcessor.headerList.get(i).getValue();
        }
        return headers;
    }
}
