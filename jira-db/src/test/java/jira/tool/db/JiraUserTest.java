package jira.tool.db;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class JiraUserTest {
    @Test
    public void testFindUser() {
        Map<String, Boolean> mapIO = new HashMap<String, Boolean>() {{
            put(null, false);
            put("", false);
            put("xjx_6639", false);
            put("cheshuaiming ", true);
        }};

        for (Map.Entry<String, Boolean> io : mapIO.entrySet()) {
            JiraUser ret = JiraUser.findUser(io.getKey());
            Assert.assertEquals(io.getValue(), ret != null);
        }
    }
}
