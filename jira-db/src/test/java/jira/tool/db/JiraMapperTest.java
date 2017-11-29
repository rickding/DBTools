package jira.tool.db;

import jira.tool.db.mapper.JiraMapper;
import jira.tool.db.model.Story;
import jira.tool.db.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class JiraMapperTest {
    private JiraMapper mapper = null;

    @Before
    public void setUp() throws Exception {
        mapper = DB.getDb().getMapper(JiraMapper.class);
    }

    @Test
    public void testGetUserList() {
        Assert.assertNotNull(mapper);

        List<User> ret = mapper.getUserList();
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetStoryList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getStoryList();
        Assert.assertNotNull(ret);
    }
}
