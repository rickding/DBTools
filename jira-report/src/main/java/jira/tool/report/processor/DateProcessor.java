package jira.tool.report.processor;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Date;

public class DateProcessor implements ValueProcessor {
    public boolean accept(String header) {
        return !StrUtils.isEmpty(header) && header.equalsIgnoreCase(HeaderProcessor.resolveDateHeader.getName());
    }

    public void process(String value, Cell cell) {
        if (cell == null) {
            return;
        }

        if (!StrUtils.isEmpty(value)) {
            Date date = DateUtils.parse(value, "yyyy/MM/dd HH:mm");
            if (date != null) {
                // Keep date only
                cell.setCellValue(DateUtils.format(date, "yyyy-MM-dd"));
                return;
            }
        }
        cell.setCellValue(value);
    }
}
