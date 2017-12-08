package jira.tool.ea;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;

import java.util.Date;

public class EADateUtil {
    private static String[] EA_Date_Format_Array = new String[] {
            "dd-MMM-yyyy HH:mm:ss", "dd-MM月-yyyy HH:mm:ss",
            "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd", "yyyyMMdd",
    };
    private static String strToday = DateUtils.format(new Date(), "yyyyMMdd");
    public static String Date_Skip = "20171123";

    public static Date parse(String strDate) {
        if (StrUtils.isEmpty(strDate)) {
            return null;
        }
        return DateUtils.parse(strDate.trim(), EA_Date_Format_Array);
    }

    public static String format(String strDate) {
        return format(strDate, "yyyyMMdd");
    }

    public static String format(String strDate, String format) {
        Date date = parse(strDate);
        if (date == null) {
            System.out.printf("Error when parse date: %s\r\n", strDate);
            return null;
        }
        return DateUtils.format(date, format);
    }

    public static boolean needsToBeProcessed(String strDate) {
        return needsToBeProcessed(strDate, strToday);
    }

    public static boolean needsToBeProcessed(String modifyDate, String processDate) {
        Date date = parse(modifyDate);
        if (date == null) {
            System.out.printf("Error when parse modify date: %s\r\n", modifyDate);
            return false;
        }
        modifyDate = DateUtils.format(date, "yyyyMMdd");

        date = parse(processDate);
        if (date == null) {
            System.out.printf("Error when parse process date: %s\r\n", processDate);
            return false;
        }
        processDate = DateUtils.format(date, "yyyyMMdd");

        return processDate.compareTo(modifyDate) >= 0 && Date_Skip.compareTo(modifyDate) <= 0;
    }

    public static String processDueDate(String value, Date today) {
        if (StrUtils.isEmpty(value) || StrUtils.isEmpty(value.trim())) {
            return null;
        }

        value = value.trim();
        if ("1.0".equalsIgnoreCase(value) || "1.".equalsIgnoreCase(value) || "1".equalsIgnoreCase(value)) {
            return null;
        }

        for (String format : new String[]{
                "yyyy.MM.dd", "yyyy-MM-dd", "yyyy/MM/dd", "yyyy年MM月dd日", "yyyy年MM月dd号",
                "yy.MM.dd", "yy-MM-dd", "yy/MM/dd", "yy年MM月dd日", "yy年MM月dd号",
                "MM.dd", "MM-dd", "MM/dd", "MM月dd日", "MM月dd号",
                "yyyyMMdd", "yyMMdd", "yyyy/MMdd", "yy/MMdd", "MMdd",
        }) {
            Date date = DateUtils.parse(value, format, false);
            if (date != null) {
                // Adjust the year if it's not set
                String strYear = DateUtils.format(date, "yyyy");
                if ("2017".compareTo(strYear) > 0) {
                    String strDate = DateUtils.format(date, "MM-dd");
                    int year = Integer.valueOf(DateUtils.format(today, "yyyy"));
                    int month = Integer.valueOf(DateUtils.format(today, "MM"));
                    if (month >= 12 && strDate.compareTo(DateUtils.format(today, "MM-dd")) < 0) {
                        year++;
                    }
                    date = DateUtils.parse(String.format("%4d-%s", year, strDate), "yyyy-MM-dd");
                }

                // Format date
                return DateUtils.format(date, EA2Jira.Jira_Date_Format);
            }
        }
        return null;
    }
}
