package jira.tool.db;

import jira.tool.db.model.Story;
import jira.tool.db.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DBUtilTest {
    @Test
    public void testGetNoDueDateStoryList() {
        List<Story> ret = DBUtil.getNoDueDateStoryList();
        System.out.printf("No due-date stories from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void getGetDevelopedStoryList() {
        List<Story> ret = DBUtil.getDevelopedStoryList();
        System.out.printf("Develop finished stories from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetUnDevelopedStoryList() {
        List<Story> ret = DBUtil.getUnDevelopedStoryList();
        System.out.printf("Un-developed stories from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void getGetReleasePlanStoryList() {
        List<Story> ret = DBUtil.getReleasePlanStoryList();
        System.out.printf("Plan to release stories from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetStartPlanStoryList() {
        List<Story> ret = DBUtil.getStartPlanStoryList();
        System.out.printf("Plan to start stories from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetReleasedStoryList() {
        List<Story> ret = DBUtil.getReleasedStoryList();
        System.out.printf("Released stories from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetPMOStoryList() {
        List<Story> ret = DBUtil.getPMOStoryList();
        System.out.printf("PMO stories from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetEpicList() {
        List<Story> ret = DBUtil.getEpicList();
        System.out.printf("Epics from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetUserList() {
        List<User> ret = DBUtil.getUserList();
        System.out.printf("Users from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }
}
