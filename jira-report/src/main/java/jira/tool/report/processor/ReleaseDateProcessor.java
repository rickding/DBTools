package jira.tool.report.processor;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Calendar;
import java.util.Date;

public class ReleaseDateProcessor implements ValueProcessor {
    public static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");

    public static int getLeftWorkDays(String date1, String date2) {
        int days = DateUtils.diffDays(date1, date2);

        // Adjust the release date
        days += 2;
        if (days < 0) {
            days = 0;
        } else if (days > 5) {
            days = 5;
        }
        return days;
    }

    public boolean accept(String header) {
        return !StrUtils.isEmpty(header) && header.equalsIgnoreCase(HeaderProcessor.releaseDateHeader.getName());
    }

    public void process(String value, Cell cell) {
        if (cell == null) {
            return;
        }
        cell.setCellValue(process(value, true));
    }

    public String process(String value, boolean checkDelay) {
        if (StrUtils.isEmpty(value)) {
            return value;
        }

        Date date = DateUtils.parse(value, "yyyy/MM/dd HH:mm a");
        if (date == null) {
            return value;
        }

        // Keep date only
        date = DateUtils.parse(DateUtils.format(date, "yyyy-MM-dd"), "yyyy-MM-dd");

        // The delay will be set as today
        if (checkDelay && date.compareTo(today) < 0) {
            date = today;
        }

        // Adjust the release date to every Wednesday
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_WEEK);
        day = (Calendar.WEDNESDAY - day + 7) % 7;
        if (day == 0) {
            day = 7;
        }
        cal.add(Calendar.DATE, day);

        return DateUtils.format(cal.getTime(), "yyyy-MM-dd");
    }
}
