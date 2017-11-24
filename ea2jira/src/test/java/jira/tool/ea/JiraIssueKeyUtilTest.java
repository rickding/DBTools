package jira.tool.ea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JiraIssueKeyUtilTest {
    @Test
    public void testIsValid() {
        Map<String, Boolean> mapIO = new HashMap<String, Boolean>() {{
            put(null, false);
            put("", false);
            put("ABC-332", true);
            put("DDX-5503", true);
            put("fnx-553", true);
            put("xjx_6639", false);
        }};

        for (Map.Entry<String, Boolean> io : mapIO.entrySet()) {
            boolean ret = JiraIssueKeyUtil.isValid(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
