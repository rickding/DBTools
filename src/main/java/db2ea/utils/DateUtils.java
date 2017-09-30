package db2ea.utils;

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
}
