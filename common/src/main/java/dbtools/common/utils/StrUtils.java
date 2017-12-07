package dbtools.common.utils;

/**
 * Created by user on 2017/9/23.
 */
public class StrUtils {
    public static boolean isEmpty(String str) {
        return str == null || str.trim().length() <= 0;
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
