package jira.tool.db;

import jira.tool.db.mapper.UserMapper;
import jira.tool.db.model.User;

import java.util.List;

public class UserUtil {
    private static List<User> userList = null;

    public static List<User> getUserList() {
        synchronized ("getUserList") {
            if (userList == null || userList.size() <= 0) {
                userList = DB.getDb().getMapper(UserMapper.class).getUserList();
            }
        }
        return userList;
    }
}
