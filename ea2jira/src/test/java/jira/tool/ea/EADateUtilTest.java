package jira.tool.ea;

import dbtools.common.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EADateUtilTest {
    @Test
    public void testParse() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put("07-十一月-2017 21:16:44", "2017-11-07");
            put("21-十一月-2017 13:01:07", "2017-11-21");
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            Date ret = EADateUtil.parse(io.getKey());
            Assert.assertEquals(io.getValue(), DateUtils.format(ret, "yyyy-MM-dd"));
        }
    }
}
