package jira.tool.db.mapper;


import jira.tool.db.model.Story;
import jira.tool.db.model.User;

import java.util.List;

public interface JiraMapper {
    List<User> getUserList();
    List<Story> getStoryList();
    List<Story> getEAGUIDList();
    List<Story> getPMOLabelList();
}
