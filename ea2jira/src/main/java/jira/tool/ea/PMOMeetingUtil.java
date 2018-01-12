package jira.tool.ea;

import dbtools.common.utils.DateUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class PMOMeetingUtil {
    private static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
    private static String strToday = DateUtils.format(today, "MMdd");

    public static boolean needsToBeProcessed(File f) {
        if (f == null) {
            return false;
        }

        String fileName = f.getName();
        String fileDate = DateUtils.format(new Date(f.lastModified()), "yyyyMMdd");
//        if (!EADateUtil.needsToBeProcessed(PMOMeetingUtil.getLastMeetingDate(), fileDate)) {
//        System.out.printf("Skip file: %s, %s, %s\r\n", PMOMeetingUtil.getLastMeetingDate(), fileDate, fileName);
            if (!EADateUtil.needsToBeProcessed(PMOMeetingUtil.getLastMonday(), fileDate)) {
            System.out.printf("Skip file: %s, %s, %s\r\n", PMOMeetingUtil.getLastMonday(), fileDate, fileName);
            return false;
        }

        String fileExt= ".csv";
        if (fileName.toLowerCase().endsWith(fileExt)) {
            // Read date in the file name
            int extIndex = fileName.indexOf(fileExt);
            if (extIndex < 4) {
                return false;
            }

            // Skip the old files
            String strDate = fileName.substring(extIndex - 4, extIndex);
            if (strDate.compareTo(strToday) != 0) {
                return false;
            }
        }
        return true;
    }

    public static String getLastMeetingDate() {
        return getLastMeetingDate(today);
    }

    public static String getLastMeetingDate(Date date) {
        if (date == null) {
            return null;
        }

        // Get the last meeting: Tuesday or Friday
        int day = DateUtils.dayOfWeek(date);
        if (day > Calendar.TUESDAY && day <= Calendar.FRIDAY) {
            day = day - Calendar.TUESDAY;
        } else {
            day = (day - Calendar.FRIDAY + 7) % 7;
        }
        return DateUtils.format(DateUtils.adjustDate(date, -day), "yyyyMMdd");
    }

    public static String getLastMonday() {
        return getLastMonday(today);
    }

    public static String getLastMonday(Date date) {
        if (date == null) {
            return null;
        }

        int day = DateUtils.dayOfWeek(date);
        day = day - Calendar.MONDAY + 7;
        return DateUtils.format(DateUtils.adjustDate(date, -day), "yyyyMMdd");
    }

}
