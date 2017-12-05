package dbtools.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by user on 2017/9/23.
 */
public class DateUtils {
    public static String format(Date date) {
        return format(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String format(Date date, String format) {
        if (date == null || StrUtils.isEmpty(format)) {
            return "";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            System.out.printf("%s, %s\r\n", e.getMessage(), format);
        }
        return "";
    }

    public static Date parse(String value, String[] formatArray) {
        return parse(value, formatArray, false);
    }

    public static Date parse(String value, String[] formatArray, boolean showError) {
        if (!StrUtils.isEmpty(value) && formatArray != null && formatArray.length > 0) {
            for (String format : formatArray) {
                Date date = DateUtils.parse(value, format, showError);
                if (date != null) {
                    return date;
                }
            }
        }

        if (showError) {
            System.out.printf("Error when parseDate: %s, %s\r\n", value, Arrays.asList(formatArray).toString());
        }
        return null;
    }

    public static Date parse(String str) {
        return parse(str, "yyyy-MM-dd HH:mm:ss", true);
    }
    public static Date parse(String str, String format) {
        return parse(str, format, true);
    }

    public static Date parse(String str, String format, boolean showError) {
        if (StrUtils.isEmpty(str) || StrUtils.isEmpty(str.trim()) || StrUtils.isEmpty(format)) {
            return null;
        }

        try {
            DateFormat df = new SimpleDateFormat(format);
            return df.parse(str.trim());
        } catch (ParseException e) {
            if (showError) {
                System.out.printf("%s, %s\r\n", e.getMessage(), format);
            }
        }
        return null;
    }

    public static int diffDays(String date1, String date2) {
        return diffDays(DateUtils.parse(date1, "yyyy-MM-dd"), DateUtils.parse(date2, "yyyy-MM-dd"));
    }

    public static int diffDays(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return 0;
        }
        return (int) ((d1.getTime() - d2.getTime()) / (1000 * 3600 * 24));
    }

    public static int dayOfWeek(String strDate) {
        Date date = parse(strDate, "yyyy-MM-dd");
        if (date == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }

    public static Date adjustDate(Date date, int days) {
        if (date == null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
