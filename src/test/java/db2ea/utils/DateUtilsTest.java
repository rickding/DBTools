package db2ea.utils;

import org.junit.Assert;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by user on 2017/9/23.
 */
public class DateUtilsTest {
    @Test
    public void testFormat() {
        SimpleDateFormat sdf = new SimpleDateFormat();
        final Calendar cal = Calendar.getInstance();
        cal.setTimeZone(sdf.getTimeZone());

        cal.set(Calendar.YEAR, 2017);
        cal.set(Calendar.MONTH, 8);
        cal.set(Calendar.DATE, 23);
        cal.set(Calendar.HOUR, 5);
        cal.set(Calendar.MINUTE, 57);
        cal.set(Calendar.SECOND, 11);
        cal.set(Calendar.MILLISECOND, 12);
        cal.set(Calendar.AM_PM, Calendar.AM);

        final Date date = cal.getTime();

        Map<Object[], String> mapIO = new HashMap<Object[], String>() {{
            put(new Object[] {null, null}, "");
            put(new Object[] {date, null}, "");
            put(new Object[] {date, "yyyy"}, String.format("%04d", cal.get(Calendar.YEAR)));
            put(new Object[] {date, "yyyy-MM-dd hh:mm:sszzz"}, "2017-09-23 05:57:11CST");
        }};

        for (Map.Entry<Object[], String> io : mapIO.entrySet()) {
            Object[] params = io.getKey();
            String ret = DateUtils.format((Date) params[0], (String) params[1]);
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
