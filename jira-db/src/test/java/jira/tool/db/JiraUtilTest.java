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
    public void testGetStoryList() {
        List<Story> ret = JiraUtil.getStoryList();
        System.out.printf("Stories from JiraUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }
}
