package jira.tool.db;

import jira.tool.db.model.Story;
import jira.tool.db.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class JiraUtilTest {
    @Test
    public void testGetNoDueDateStoryList() {
        List<Story> ret = JiraUtil.getNoDueDateStoryList();
        System.out.printf("No due-date stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void getGetDevelopedStoryList() {
        List<Story> ret = JiraUtil.getDevelopedStoryList();
        System.out.printf("Develop finished stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetUnDevelopedStoryList() {
        List<Story> ret = JiraUtil.getUnDevelopedStoryList();
        System.out.printf("Un-developed stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void getGetReleasePlanStoryList() {
        List<Story> ret = JiraUtil.getReleasePlanStoryList();
        System.out.printf("Plan to release stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetStartPlanStoryList() {
        List<Story> ret = JiraUtil.getStartPlanStoryList();
        System.out.printf("Plan to start stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetReleasedStoryList() {
        List<Story> ret = JiraUtil.getReleasedStoryList();
        System.out.printf("Released stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetPMOStoryList() {
        List<Story> ret = JiraUtil.getPMOStoryList();
        System.out.printf("PMO stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetUserList() {
        List<User> ret = JiraUtil.getUserList();
        System.out.printf("Users from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }
}
