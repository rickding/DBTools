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
}
