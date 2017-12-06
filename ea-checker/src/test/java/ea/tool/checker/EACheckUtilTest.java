package ea.tool.checker;

import dbtools.common.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class EACheckUtilTest {
    @Test
    public void testGetLastMeetingDate() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put(null, null);
            put("2017-12-04", "20171201");
            put("2017-12-05", "20171201");
            put("2017-12-06", "20171205");
            put("2017-12-07", "20171205");
            put("2017-12-08", "20171205");
            put("2017-12-09", "20171208");
            put("2017-12-10", "20171208");
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            String ret = EACheckUtil.getLastMeetingDate(DateUtils.parse(io.getKey(), "yyyy-MM-dd"));
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
