package jira.tool.jira;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JiraStatusEnumTest {
    @Test
    public void testIsClosed() {
        Map<String, Boolean> mapIO = new HashMap<String, Boolean>() {{
            put(null, false);
            put("", false);
            put("test", false);
            put("pending", true);
            put("Pending", true);
            put("PENDING", true);
            put("关闭", true);
            put("Done", false);
        }};

        for (Map.Entry<String, Boolean> io : mapIO.entrySet()) {
            Boolean ret = JiraStatusEnum.isClosed(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
