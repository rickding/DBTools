package dbtools.common.utils;

/**
 * Created by user on 2017/9/23.
 */
public class StrUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() <= 0;
    }

    public static boolean contains(String str, String subStr) {
        return contains(str, subStr, ",");
    }

    public static boolean contains(String str, String subStr, String separator) {
        if (subStr == null || subStr.length() <= 0) {
            return false;
        }

        if (subStr.equalsIgnoreCase(str)) {
            return true;
        }

        String[] strArray = split(str, separator);
        if (ArrayUtils.isEmpty(strArray)) {
            return false;
        }

        for (String tmpStr : strArray) {
            if (tmpStr.trim().length() == 0 && subStr.trim().length() == 0) {
                return true;
            }

            if (tmpStr.trim().equalsIgnoreCase(subStr.trim())) {
                return true;
            }
        }
        return false;
    }

    public static String[] split(String str, String separator) {
        if (StrUtils.isEmpty(str) || separator == null || separator.length() <= 0) {
            return null;
        }

        return str.split(separator);
    }

    public static String combine(String[] strArray, String separator) {
        if (ArrayUtils.isEmpty(strArray) || separator == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String str : strArray) {
            sb.append(separator);
            sb.append(str);
        }

        return sb.substring(separator.length());
    }
}
