package jira.tool.ea;

import ea.tool.api.JiraUserInterface;
import jira.tool.db.JiraUser;

public class JiraUserImpl implements JiraUserInterface {
    public String findUser(String name) {
        JiraUser user = JiraUser.findUser(name);
        if (user != null) {
            return user.getName();
        }
        return null;
    }

    public String findQA(String name) {
        JiraUser user = JiraUser.findQA(name);
        if (user != null) {
            return user.getName();
        }
        return null;
    }
}
