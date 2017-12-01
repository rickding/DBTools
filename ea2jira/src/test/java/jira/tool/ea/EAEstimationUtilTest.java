package jira.tool.ea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class EAEstimationUtilTest {
    @Test
    public void testProcessEstimation() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put(null, null);
            put("", null);
            put("3h", String.valueOf(3 * 3600));
            put("2d", String.valueOf(2 * 8 * 3600));
            put("1w", String.valueOf(5 * 8 * 3600));
            put("0.1h", String.valueOf((int)(0.1 * 3600)));
            put("1.2d", String.valueOf((int)(1.2 * 8 * 3600)));
            put("2.3w", String.valueOf((int)(2.3 * 5 * 8 * 3600)));
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            String ret = EAEstimationUtil.processEstimation(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
