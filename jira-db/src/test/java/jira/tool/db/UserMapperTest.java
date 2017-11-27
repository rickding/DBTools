package jira.tool.db;

import jira.tool.db.mapper.UserMapper;
import jira.tool.db.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class UserMapperTest {
    @Test
    public void testUserAccount() {
        UserMapper mapper = DB.getDb().getMapper(UserMapper.class);
        Assert.assertNotNull(mapper);

        List<User> user = mapper.getUserList();
        Assert.assertNotNull(user);
    }
}
