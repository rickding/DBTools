package jira.tool.db;

import jira.tool.db.model.Story;
import jira.tool.db.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class JiraUtilTest {
    @Test
    public void testGetUserList() {
        List<User> ret = JiraUtil.getUserList();
        System.out.printf("Users from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetPMOStoryList() {
        List<Story> ret = JiraUtil.getPMOStoryList();
        System.out.printf("PMO stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetResolvedStoryList() {
        List<Story> ret = JiraUtil.getResolvedStoryList();
        System.out.printf("Resolved stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }
}
