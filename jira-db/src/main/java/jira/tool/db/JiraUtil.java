package jira.tool.db;

import jira.tool.db.mapper.JiraMapper;
import jira.tool.db.model.Story;
import jira.tool.db.model.User;

import java.util.List;

public class JiraUtil {
    private static List<User> userList = null;

    public static List<User> getUserList() {
        synchronized ("getUserList") {
            if (userList == null || userList.size() <= 0) {
                userList = DB.getDb().getMapper(JiraMapper.class).getUserList();
            }
        }
        return userList;
    }

    public static List<Story> getStoryList() {
        synchronized ("getStoryList") {
            return DB.getDb().getMapper(JiraMapper.class).getStoryList();
        }
    }
}
