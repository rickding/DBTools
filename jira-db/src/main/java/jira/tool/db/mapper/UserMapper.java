package jira.tool.db.mapper;


import jira.tool.db.model.User;

import java.util.List;

public interface UserMapper {
    List<User> getUserList();
}
