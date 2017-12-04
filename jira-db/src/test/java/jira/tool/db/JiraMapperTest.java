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
    public void getGetReleasePlanStoryList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getReleasePlanStoryList();
        System.out.printf("Plan to release stories: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetStartPlanStoryList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getStartPlanStoryList();
        System.out.printf("Plan to start stories: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetResolvedStoryList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getReleasedStoryList();
        System.out.printf("Resolved stories: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetStartDateList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getStartDateList();
        System.out.printf("Started dates: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void getGetReleaseDateList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getReleaseDateList();
        System.out.printf("Released dates: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetCustomerList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getCustomerList();
        System.out.printf("Customers: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetCustomerOptionList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getCustomerOptionList();
//        System.out.printf("Customer options: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetPMOStoryList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getPMOStoryList();
        System.out.printf("PMO stories: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetEAGUIDList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getEAGUIDList();
        System.out.printf("EAGUIDs: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetPMOLabelList() {
        Assert.assertNotNull(mapper);

        List<Story> ret = mapper.getPMOLabelList();
        System.out.printf("PMO Labels: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetUserList() {
        Assert.assertNotNull(mapper);

        List<User> ret = mapper.getUserList();
        System.out.printf("Users: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }
}
