package dbtools.common.utils;

public class DoubleUtil {
    public static double format(double value, int digits) {
        if (digits < 0) {
            return value;
        }
        return Double.valueOf(String.format("%." + digits + "f", value));
    }
}
