package ea.tool.api;

public class EAEstimationUtil {
    public static String processEstimation(String value) {
        return processEstimation(value, true);
    }

    public static String processEstimation(String value, boolean showError) {
        if (value == null || value.trim().length() <= 0) {
            return null;
        }

        String[] values = value.trim().toLowerCase().split(" ");
        double estimation = 0.0;

        for (String str : values) {
            value = str;
            try {
                int base = 0;
                double v = 0.0;
                if (value.endsWith("min") || value.endsWith("minute") || value.endsWith("minutes")) {
                    v = Double.valueOf(value.substring(0, value.indexOf("min")));
                    base = 60;
                } else if (value.endsWith("h") || value.endsWith("hour") || value.endsWith("hours") || value.endsWith("hr")) {
                    v = Double.valueOf(value.substring(0, value.indexOf("h")));
                    base = 3600;
                } else if (value.endsWith("d") || value.endsWith("day") || value.endsWith("days")) {
                    v = Double.valueOf(value.substring(0, value.indexOf("d")));
                    base = 8 * 3600;
                } else if (value.endsWith("w") || value.endsWith("week") || value.endsWith("weeks")) {
                    v = Double.valueOf(value.substring(0, value.indexOf("w")));
                    base = 5 * 8 * 3600;
                } else {
                    v = Double.valueOf(value);
                    base = 8 * 3600; // default as a day
                }
                estimation += v * base;
            } catch (Exception e) {
                if (showError) {
                    System.out.printf("Error when process estimation: %s\r\n", value);
                }
            }
        }

        int tmp = (int) (estimation);
        return tmp <= 0 ? null : String.format("%d", tmp);
    }
}
