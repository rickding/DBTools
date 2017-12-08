package jira.tool.report.processor;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SprintDateProcessor implements ValueProcessor {
    public static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");

    public static int getLeftWorkDays(String date, String today) {
        Date beforeAdjustDate = DateUtils.parse(date, "yyyy-MM-dd");
        Date dateDate = getSprintDate(beforeAdjustDate, false);
        Date todayDate = DateUtils.parse(today, "yyyy-MM-dd");

        int days = DateUtils.diffDays(dateDate, todayDate);
        if (days <= 1) {
            days = 0;
        } else {
            if (DateUtils.dayOfWeek(todayDate) == Calendar.FRIDAY
                    && DateUtils.diffDays(beforeAdjustDate, todayDate) == 1
                    && DateUtils.diffDays(beforeAdjustDate, dateDate) == -7) {
                // It's special when report is generated on Friday
                days = 0;
            } else {
                days = 5;
            }
        }
        return days;
    }

    protected List<String> dateFormatList = new ArrayList<String>() {{
        add("yyyy/MM/dd HH:mm:ss");
        add("yyyy/MM/dd HH:mm a");
        add("yyyy/MM/dd HH:mm");
        add("yyyy/MM/dd");
        add("yyyy-MM-dd HH:mm:ss");
        add("yyyy-MM-dd");
    }};

    protected static int sprintEnd = Calendar.SATURDAY;
    protected boolean adjustDelay = true;

    protected List<HeaderProcessor> acceptedHeaderList = new ArrayList<HeaderProcessor>() {{
        add(HeaderProcessor.dueDateHeader);
    }};

    public boolean accept(String header) {
        if (!StrUtils.isEmpty(header) && acceptedHeaderList != null && acceptedHeaderList.size() > 0) {
            for (HeaderProcessor processor : acceptedHeaderList) {
                if (header.equalsIgnoreCase(processor.getName())) {
                    return true;
                }
            }
        }
        return false;
    }

    public void process(String value, Cell cell) {
        if (cell == null) {
            return;
        }
        cell.setCellValue(process(value, adjustDelay));
    }

    public Date parseDate(String value) {
        String[] formatArray = new String[dateFormatList.size()];
        dateFormatList.toArray(formatArray);

        Date date = DateUtils.parse(value, formatArray, false);
        if (date == null) {
            System.out.printf("Error when parseDate: %s, %s\r\n", value, dateFormatList.toString());
        }
        return date;
    }

    public String process(String value, boolean adjustDelay) {
        if (StrUtils.isEmpty(value) || dateFormatList == null || dateFormatList.size() <= 0) {
            return value;
        }

        Date date = parseDate(value);
        if (date == null) {
            return value;
        }
        date = getSprintDate(date, adjustDelay);
        return DateUtils.format(date, "yyyy-MM-dd");
    }

    public static Date getSprintDate(Date date, boolean adjustDelay) {
        if (date == null) {
            return null;
        }

        // Keep date only
        date = DateUtils.parse(DateUtils.format(date, "yyyy-MM-dd"), "yyyy-MM-dd");

        // The delay will be set as today
        if (adjustDelay && date.compareTo(today) < 0) {
            date = today;
        }

        // Adjust the release date to every Wednesday
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int day = cal.get(Calendar.DAY_OF_WEEK);
        day = (sprintEnd - day + 7) % 7;
        if (day == 0) {
            day = 7;
        }
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }
}
