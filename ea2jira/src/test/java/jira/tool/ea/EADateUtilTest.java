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
            put("27-11月-2017 16:26:42", "2017-11-27");
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            Date ret = EADateUtil.parse(io.getKey());
            Assert.assertEquals(io.getValue(), DateUtils.format(ret, "yyyy-MM-dd"));
        }
    }

    @Test
    public void testProcessDueDate() {
        Date today = DateUtils.parse("2017-12-05", "yyyy-MM-dd");
        Map<String, String> mapIO = new HashMap<String, String>(){{
            put("1-15", "2018/01/15");
            put("12.28", "2017/12/28");
            put("12月18日", "2017/12/18");
            put("2017-12-15", "2017/12/15");
            put("2018-1-3", "2018/01/03");
            put("20171230", "2017/12/30");
            put("1208", "2017/12/08");
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            String ret = EADateUtil.processDueDate(io.getKey(), today);
            Assert.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testProcessDueDate2() {
        Date today = DateUtils.parse("2018-1-5", "yyyy-MM-dd");
        Map<String, String> mapIO = new HashMap<String, String>(){{
            put("1-15", "2018/01/15");
            put("12.28", "2018/12/28");
            put("12月18日", "2018/12/18");
            put("2017-12-15", "2017/12/15");
            put("2018-1-3", "2018/01/03");
            put("1208", "2018/12/08");
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            String ret = EADateUtil.processDueDate(io.getKey(), today);
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
