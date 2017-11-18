package jira.tool.report.processor;

import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Cell;

public class TimeProcessor implements ValueProcessor {
    public boolean accept(String header) {
        return !StrUtils.isEmpty(header) && header.equalsIgnoreCase("Time");
    }

    public void process(String value, Cell cell) {
        if (cell == null) {
            return;
        }

        if (!StrUtils.isEmpty(value)) {
            try {
                Double time = Double.valueOf(value);
                time = time / (8 * 3600);
                cell.setCellValue((time));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cell.setCellValue(0.0);
    }
}
