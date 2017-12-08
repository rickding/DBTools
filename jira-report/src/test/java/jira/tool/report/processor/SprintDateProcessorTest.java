package jira.tool.report.processor;

import dbtools.common.utils.DateUtils;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SprintDateProcessorTest {
    private SprintDateProcessor processor = new SprintDateProcessor();

    @Test
    public void testParseDate() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put("2017/11/17 5:43 下午", "2017-11-17"); // 解决
            put("2017/11/07 12:00 上午", "2017-11-07"); // 到期日
            put("2017/11/02 12:00 上午", "2017-11-02"); // 计划开始日期
            put("2017/11/20 6:11 下午", "2017-11-20"); // 实际上线日期
            put("2017-12-04 10:12:55", "2017-12-04"); // DB
        }};

        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            Date ret = processor.parseDate(io.getKey());
            Assert.assertEquals(io.getValue(), DateUtils.format(ret, "yyyy-MM-dd"));
        }
    }

    @Test
    public void testGetLeftDays() {
        Map<String[], Integer> mapIO = new HashMap<String[], Integer>() {{
            // Run on Friday, one day before sprint end
            put(new String[]{"2017-11-27", "2017-12-01"}, 0);
            put(new String[]{"2017-11-28", "2017-12-01"}, 0);
            put(new String[]{"2017-11-29", "2017-12-01"}, 0);
            put(new String[]{"2017-11-30", "2017-12-01"}, 0);
            put(new String[]{"2017-12-01", "2017-12-01"}, 0);
            put(new String[]{"2017-12-02", "2017-12-01"}, 0);
            put(new String[]{"2017-12-03", "2017-12-01"}, 5);

            put(new String[]{"2017-12-04", "2017-12-01"}, 5);
            put(new String[]{"2017-12-05", "2017-12-01"}, 5);
            put(new String[]{"2017-12-06", "2017-12-01"}, 5);
            put(new String[]{"2017-12-07", "2017-12-01"}, 5);
            put(new String[]{"2017-12-08", "2017-12-01"}, 5);
            put(new String[]{"2017-12-09", "2017-12-01"}, 5);
            put(new String[]{"2017-12-10", "2017-12-01"}, 5);

            // Run on Saturday, the day of sprint end
            put(new String[]{"2017-11-27", "2017-12-02"}, 0);
            put(new String[]{"2017-11-28", "2017-12-02"}, 0);
            put(new String[]{"2017-11-29", "2017-12-02"}, 0);
            put(new String[]{"2017-11-30", "2017-12-02"}, 0);
            put(new String[]{"2017-12-01", "2017-12-02"}, 0);
            put(new String[]{"2017-12-02", "2017-12-02"}, 5);
            put(new String[]{"2017-12-03", "2017-12-02"}, 5);

            put(new String[]{"2017-12-04", "2017-12-02"}, 5);
            put(new String[]{"2017-12-05", "2017-12-02"}, 5);
            put(new String[]{"2017-12-06", "2017-12-02"}, 5);
            put(new String[]{"2017-12-07", "2017-12-02"}, 5);
            put(new String[]{"2017-12-08", "2017-12-02"}, 5);
            put(new String[]{"2017-12-09", "2017-12-02"}, 5);
            put(new String[]{"2017-12-10", "2017-12-02"}, 5);

            // Run on Sunday, the day after the sprint end
            put(new String[]{"2017-11-27", "2017-12-03"}, 0);
            put(new String[]{"2017-11-28", "2017-12-03"}, 0);
            put(new String[]{"2017-11-29", "2017-12-03"}, 0);
            put(new String[]{"2017-11-30", "2017-12-03"}, 0);
            put(new String[]{"2017-12-01", "2017-12-03"}, 0);
            put(new String[]{"2017-12-02", "2017-12-03"}, 5);
            put(new String[]{"2017-12-03", "2017-12-03"}, 5);

            put(new String[]{"2017-12-04", "2017-12-03"}, 5);
            put(new String[]{"2017-12-05", "2017-12-03"}, 5);
            put(new String[]{"2017-12-06", "2017-12-03"}, 5);
            put(new String[]{"2017-12-07", "2017-12-03"}, 5);
            put(new String[]{"2017-12-08", "2017-12-03"}, 5);
            put(new String[]{"2017-12-09", "2017-12-03"}, 5);
            put(new String[]{"2017-12-10", "2017-12-03"}, 5);

            // Run on Monday
            put(new String[]{"2017-11-27", "2017-12-04"}, 0);
            put(new String[]{"2017-11-28", "2017-12-04"}, 0);
            put(new String[]{"2017-11-29", "2017-12-04"}, 0);
            put(new String[]{"2017-11-30", "2017-12-04"}, 0);
            put(new String[]{"2017-12-01", "2017-12-04"}, 0);
            put(new String[]{"2017-12-02", "2017-12-04"}, 5);
            put(new String[]{"2017-12-03", "2017-12-04"}, 5);

            put(new String[]{"2017-12-04", "2017-12-04"}, 5);
            put(new String[]{"2017-12-05", "2017-12-04"}, 5);
            put(new String[]{"2017-12-06", "2017-12-04"}, 5);
            put(new String[]{"2017-12-07", "2017-12-04"}, 5);
            put(new String[]{"2017-12-08", "2017-12-04"}, 5);
            put(new String[]{"2017-12-09", "2017-12-04"}, 5);
            put(new String[]{"2017-12-10", "2017-12-04"}, 5);

            put(new String[]{"2017-12-09", "2017-12-08"}, 0);
        }};

        for (Map.Entry<String[], Integer> io : mapIO.entrySet()) {
            String[] params = io.getKey();
            int ret = SprintDateProcessor.getLeftWorkDays(params[0], params[1]);
            if (io.getValue().intValue() != ret) {
                System.out.printf("%s, %s, expected: %d, actual: %d\r\n", params[0], params[1], io.getValue(), ret);
                ret = SprintDateProcessor.getLeftWorkDays(params[0], params[1]);
            }
            Assert.assertEquals(io.getValue().intValue(), ret);
        }
    }

    @Test
    public void testProcess() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put(null, null);
            put("2017/10/24 12:00", "2017-10-28");

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
