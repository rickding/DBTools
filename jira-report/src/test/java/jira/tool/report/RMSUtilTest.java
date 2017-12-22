package jira.tool.report;

import com.alibaba.fastjson.JSONArray;
import org.junit.Test;

import java.util.ArrayList;

public class RMSUtilTest {
    @Test
    public void testGet() {
        JSONArray ret = RMSUtil.get();
        System.out.println(ret);
    }

    @Test
    public void testDelete() {
        System.out.println(RMSUtil.delete(new ArrayList<String>() {{add("0im3Jl9dzw");}}));
    }
}
