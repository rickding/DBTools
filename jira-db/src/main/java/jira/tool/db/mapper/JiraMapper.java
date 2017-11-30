package jira.tool.db.mapper;


import jira.tool.db.model.Story;
import jira.tool.db.model.User;

import java.util.List;

public interface JiraMapper {
    List<Story> getResolvedStoryList();
    List<Story> getStartDateList();
    List<Story> getCustomerList();
    List<Story> getCustomerOptionList();

    List<User> getUserList();
    List<Story> getPMOStoryList();
    List<Story> getEAGUIDList();
    List<Story> getPMOLabelList();
}
