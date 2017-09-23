package db2ea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2017/9/23.
 */
public class StrUtilsTest {
    @Test
    public void testIsEmpty() {
        Map<String, Boolean> mapIO = new HashMap<String, Boolean>() {{
            put(null, true);
            put("", true);
            put("t", false);
        }};

        for (Map.Entry<String, Boolean> io : mapIO.entrySet()) {
            boolean ret = StrUtils.isEmpty(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
