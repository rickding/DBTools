package dbtools.common.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by user on 2017/9/23.
 */
public class DateUtils {
    public static String format(Date date, String format) {
        if (date == null || StrUtils.isEmpty(format)) {
            return "";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return "";
    }

    public static Date parse(String str, String format) {
        if (StrUtils.isEmpty(str) || StrUtils.isEmpty(format)) {
            return null;
        }

        try {
            DateFormat df = new SimpleDateFormat(format);
            return df.parse(str);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }

    public static int diffDays(String date1, String date2) {
        Date d1 = DateUtils.parse(date1, "yyyy-MM-dd");
        Date d2 = DateUtils.parse(date2, "yyyy-MM-dd");
        if (d1 == null || d2 == null) {
            return 0;
        }
        return (int) (d1.getTime() - d2.getTime()) / (1000 * 3600 * 24);
    }
}
