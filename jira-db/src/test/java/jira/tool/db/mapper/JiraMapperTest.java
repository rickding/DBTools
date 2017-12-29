package jira.tool.db.mapper;

import jira.tool.db.DB;
import jira.tool.db.model.Story;
import org.junit.Test;

import java.util.List;

public class JiraMapperTest {
    @Test
    public void testGetIssueList() {
        List<Story> ret = DB.getDb().getMapper(JiraMapper.class).getIssueList();
        System.out.printf("Issues from mapper: %d\r\n", ret == null ? 0 : ret.size());
    }
}
