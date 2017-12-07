package ea.tool.api;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class EAQAUtilTest {
    @Test
    public void testFormatQA() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put("qa_test", "qa_test");
            put("李存 英", "李存英");
            put("李存 英、杨馥伊", "李存英, 杨馥伊");
            put("李存 英, 杨馥伊", "李存英, 杨馥伊");
            put("李存英、杨馥伊", "李存英, 杨馥伊");
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            String ret = EAQAUtil.formatQA(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testGetQA() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put("测试负责人: test", "test");
            put("QA负责人; test", "test");
            put("QA 负责人 test", "test");
            put("测试leader: test", "test");
            put("测试 leader  test", "test");
            put("QALeader: test", "test");
            put("QA Leader, test", "test");
            put("测试工程师: test", "test");
            put("测试工程师: test", "test");
            put("QA: test", "test");
            put("测试", null);
            put("QAtest", null);
            put("测试：李存英", "李存英");
            put("测试：李存英、杨馥伊", "李存英、杨馥伊");
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            String ret = EAQAUtil.getQA(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
