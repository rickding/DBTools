package jira.tool.report.processor;

import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Cell;

public class EstimationProcessor implements ValueProcessor {
    public boolean accept(String header) {
        return !StrUtils.isEmpty(header) && header.equalsIgnoreCase(HeaderProcessor.estimationHeader.getName());
    }

    public String process(String value, Cell cell) {
        if (cell == null) {
            return value;
        }

        if (!StrUtils.isEmpty(value)) {
            try {
                double time = Double.valueOf(value);
                time = time / (8 * 3600);
                cell.setCellValue(time);
                return String.valueOf(time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cell.setCellValue(0.0);
        return "0";
    }
}
