package jira.tool.jira;

import ea.tool.api.EAStatusEnum;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class StatusUtilTest {
    @Test
    public void testToEAStatus() {
        Map<String, EAStatusEnum> mapIO = new HashMap<String, EAStatusEnum>() {{
            put(null, null);
            put("", null);
            put("test", null);
            put("取消", EAStatusEnum.Approved);
            put("完成", EAStatusEnum.Approved);
            put("done", EAStatusEnum.Approved);
            put("已解决", EAStatusEnum.Approved);
        }};

        for (Map.Entry<String, EAStatusEnum> io : mapIO.entrySet()) {
            EAStatusEnum ret = StatusUtil.toEAStatus(io.getKey());
            Assert.assertEquals(io.getValue() == null ? null : io.getValue().getCode(), ret == null ? null : ret.getCode());
        }
    }
}
