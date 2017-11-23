package jira.tool.ea;

import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;

import java.util.Date;

public class EADateUtil {
    private static String EA_Date_Format = "dd-MMM-yyyy HH:mm:ss";
    private static String strToday = DateUtils.format(new Date(), "yyyyMMdd");

    public static Date parse(String strDate) {
        if (StrUtils.isEmpty(strDate)) {
            return null;
        }
        return DateUtils.parse(strDate, EA_Date_Format);
    }

    public static boolean needsToBeProcessed(String strDate) {
        if (StrUtils.isEmpty(strDate)) {
            return false;
        }

        Date date = parse(strDate);
        if (date == null) {
            System.out.printf("Error when parse date: %s", strDate);
            return false;
        }

        return strToday.equalsIgnoreCase(DateUtils.format(date, "yyyyMMdd"));
    }
}
