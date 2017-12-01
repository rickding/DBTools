package jira.tool.ea;

import dbtools.common.utils.StrUtils;

public class EAEstimationUtil {
    public static String processEstimation(String value) {
        if (StrUtils.isEmpty(value) || StrUtils.isEmpty(value.trim())) {
            return null;
        }

        value = value.trim().toLowerCase();
        try {
            int base = 0;
            double v = 0.0;
            if (value.endsWith("h") || value.endsWith("hour") || value.endsWith("hr")) {
                v = Double.valueOf(value.substring(0, value.length() - 1));
                base = 3600;
            } else if (value.endsWith("d") || value.endsWith("day")) {
                v = Double.valueOf(value.substring(0, value.length() - 1));
                base = 8 * 3600;
            } else if (value.endsWith("w") || value.endsWith("week")) {
                v = Double.valueOf(value.substring(0, value.length() - 1));
                base = 5 * 8 * 3600;
            } else {
                v = Double.valueOf(value);
                base = 8 * 3600; // default as a day
            }

            int tmp = (int) (v * base);
            return tmp <= 0 ? null : String.format("%d", tmp);
        } catch (Exception e) {
            System.out.printf("Error when process estimation: %s\n", value);
        }
        return null;
    }
}
