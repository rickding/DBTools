package jira.tool.report.processor;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class SprintDateProcessorTest {
    private SprintDateProcessor processor = new SprintDateProcessor();

    @Test
    public void testGetLeftDays() {
        Map<String[], Integer> mapIO = new HashMap<String[], Integer>(){{
            put(new String[]{"2017-11-15", "2017-11-15"}, 2);
            put(new String[]{"2017-11-15", "2017-11-16"}, 1);
            put(new String[]{"2017-11-15", "2017-11-17"}, 0);
            put(new String[]{"2017-11-15", "2017-11-18"}, 0);
            put(new String[]{"2017-11-15", "2017-11-19"}, 0);
            put(new String[]{"2017-11-15", "2017-11-20"}, 0);
            put(new String[]{"2017-11-15", "2017-11-21"}, 0);

            put(new String[]{"2017-11-22", "2017-11-15"}, 5);
            put(new String[]{"2017-11-22", "2017-11-16"}, 5);
            put(new String[]{"2017-11-22", "2017-11-17"}, 5);
            put(new String[]{"2017-11-22", "2017-11-18"}, 5);
            put(new String[]{"2017-11-22", "2017-11-19"}, 5);
            put(new String[]{"2017-11-22", "2017-11-20"}, 4);
            put(new String[]{"2017-11-22", "2017-11-21"}, 3);

            put(new String[]{"2017-12-13", "2017-11-16"}, 5);
        }};

        for (Map.Entry<String[], Integer> io : mapIO.entrySet()) {
            String[] params = io.getKey();
            int ret = SprintDateProcessor.getLeftWorkDays(params[0], params[1]);
            Assert.assertEquals(io.getValue().intValue(), ret);
        }
    }

    @Test
    public void testProcess() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put(null, null);
            put("2017/10/24 12:00", "2017/10/24 12:00");

            put("2018/01/04 12:00 上午", "2018-01-06");
            put("2018/01/03 12:00 上午", "2018-01-06");
            put("2018/01/02 12:00 上午", "2018-01-06");
            put("2018/01/01 12:00 上午", "2018-01-06");
            put("2017/12/31 12:00 上午", "2018-01-06");
            put("2017/12/30 12:00 上午", "2018-01-06");

            put("2017/12/07 12:00 上午", "2017-12-09");
            put("2017/12/06 12:00 上午", "2017-12-09");
            put("2017/12/05 12:00 上午", "2017-12-09");
            put("2017/12/04 12:00 上午", "2017-12-09");
            put("2017/12/03 12:00 上午", "2017-12-09");
            put("2017/12/02 12:00 上午", "2017-12-09");

            put("2017/12/01 12:00 上午", "2017-12-02");
            put("2017/11/30 12:00 上午", "2017-12-02");
            put("2017/11/29 12:00 上午", "2017-12-02");
            put("2017/11/28 12:00 上午", "2017-12-02");
            put("2017/11/27 12:00 上午", "2017-12-02");
            put("2017/11/26 12:00 上午", "2017-12-02");
            put("2017/11/25 12:00 上午", "2017-12-02");

            put("2017/11/24 12:00 上午", "2017-11-25");
            put("2017/10/24 12:00 上午", "2017-10-28");
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            String ret = processor.process(io.getKey(), false);
            Assert.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testAccept() {
        Map<String, Boolean> mapIO = new HashMap<String, Boolean>() {{
            put(null, false);
            put("", false);
            put("header name", false);
            put("Due Date", true);
            put(HeaderProcessor.dueDateHeader.getName(), true);
        }};

        for (Map.Entry<String, Boolean> io : mapIO.entrySet()) {
            boolean ret = processor.accept(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
