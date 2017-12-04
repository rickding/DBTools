package dbtools.common.utils;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class DoubleUtilTest {
    @Test
    public void testFormat() {
        Map<Double, Double> mapIO = new HashMap<Double, Double>() {{
            put(0.0, 0.0);
            put(0.02222, 0.0222);
        }};

        for (Map.Entry<Double, Double> io : mapIO.entrySet()) {
            double ret = DoubleUtil.format(io.getKey(), 4);
            Assert.assertEquals(io.getValue(), ret, 0.0);
        }
    }
}
