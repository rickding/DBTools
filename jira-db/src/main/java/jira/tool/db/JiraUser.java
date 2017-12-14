package jira.tool.db;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JiraUser {
    private static List<User> userListFromDB = null;
    private static JiraUser[] userArray = null;

    public static JiraUser findUser(String name) {
        if (userArray == null || userArray.length <= 0) {
            userArray = getUserArray();
        }

        if (StrUtils.isEmpty(name) || userArray == null || userArray.length <= 0) {
            return null;
        }

        name = name.trim();
        for (JiraUser user : userArray) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }

            for (String alias : user.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return user;
                }
            }
        }
        return JiraUserEnum.toUser(JiraUserEnum.findUser(name));
    }

    private static JiraUser[] getUserArray() {
        // Get users from db
        synchronized("getUserArray") {
            if (userListFromDB == null) {
                userListFromDB = DBUtil.getUserList();
            }
        }

        // Combine the users
        if (userListFromDB != null && userListFromDB.size() > 0) {
            List<JiraUser> tmpUserList = new ArrayList<JiraUser>(userListFromDB.size());

            for (User user : userListFromDB) {
                if (user == null || StrUtils.isEmpty(user.getName()) || StrUtils.isEmpty(user.getCode()) || StrUtils.isEmpty(user.getTeam())) {
                    continue;
                }

                JiraUser tmpUser = new JiraUser(user.getTeam().trim(), user.getCode().trim(), new String[]{user.getName().trim()});
                tmpUserList.add(tmpUser);

                // Find if it's pre-defined
                JiraUserEnum tmpUserEnum = JiraUserEnum.findUser(user.getName());
                if (tmpUserEnum == null) {
                    tmpUserEnum = JiraUserEnum.findUser(user.getCode());
                }

                if (tmpUserEnum != null) {
                    // Save the information
                    tmpUser.addAliases(tmpUserEnum.getAliases());
                }
            }

            if (tmpUserList != null && tmpUserList.size() > 0) {
                JiraUser[] tmpArray = new JiraUser[tmpUserList.size()];
                tmpUserList.toArray(tmpArray);
                return tmpArray;
            }
        }
        return null;
    }

    private String team;
    private String name;
    private List<String> aliases = new ArrayList<String>();

    public JiraUser(String team, String name, String[] aliases) {
        this.team = team;
        this.name = name;

        if (!ArrayUtils.isEmpty(aliases)) {
            this.aliases.addAll(Arrays.asList(aliases));
        }
    }

    public JiraUser(String team, String name, List<String> aliases) {
        this.team = team;
        this.name = name;

        if (aliases != null && aliases.size() > 0) {
            this.aliases.addAll(aliases);
        }
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getAliases() {
        return aliases;
    }

    public void addAliases(String[] aliases) {
        if (!ArrayUtils.isEmpty(aliases)) {
            this.aliases.addAll(Arrays.asList(aliases));
        }
    }

    public void addAliases(List<String> aliases) {
        if (aliases != null && aliases.size() > 0) {
            this.aliases.addAll(aliases);
        }
    }
}
