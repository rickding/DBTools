package jira.tool.db;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JiraUser {
    private static List<User> userListFromDB = null;
    private static JiraUser[] userArray = null;

    public static JiraUser findQA(String name) {
        JiraUser user = findUser(name);
        if (user != null) {
            return user;
        }
        return JiraQAEnum.toUser(JiraQAEnum.findQAByName(name));
    }

    public static JiraUser findUser(String name) {
        if (userArray == null || userArray.length <= 0) {
            userArray = getUserArray();
        }

        if (StrUtils.isEmpty(name) || userArray == null || userArray.length <= 0) {
            return null;
        }

        name = name.trim();
        for (JiraUser user : userArray) {
            if (user.code.equalsIgnoreCase(name)) {
                return user;
            }

            for (String alias : user.aliases) {
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

            JiraUser tmpUser = new JiraUser(JiraQAEnum.QA_Team_Name, JiraQAEnum.noQA.getName(), JiraQAEnum.noQA.getCode(), JiraQAEnum.noQA.getAliases());
            tmpUserList.add(tmpUser);

            for (User user : userListFromDB) {
                if (user == null || StrUtils.isEmpty(user.getName()) || StrUtils.isEmpty(user.getCode()) || StrUtils.isEmpty(user.getTeam())) {
                    continue;
                }

                tmpUser = new JiraUser(user.getTeam().trim(), user.getName().trim(), user.getCode().trim(), user.getAliases());
                tmpUserList.add(tmpUser);

                // Find if it's pre-defined
                JiraUserEnum tmpUserEnum = JiraUserEnum.findUser(user.getName());
                if (tmpUserEnum == null) {
                    tmpUserEnum = JiraUserEnum.findUser(user.getCode());
                }
                if (tmpUserEnum != null) {
                    tmpUser.addAliases(tmpUserEnum.getAliases());
                }

                JiraQAEnum qaUser = JiraQAEnum.findQAByName(user.getName());
                if (qaUser == null) {
                    qaUser = JiraQAEnum.findQAByName(user.getCode());
                }
                if (qaUser != null) {
                    tmpUser.addAliases(qaUser.getAliases());
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
    private String code;
    private List<String> aliases = new ArrayList<String>();

    public JiraUser(String team, String name, String code, String[] aliases) {
        this.team = team;
        this.name = name;
        this.code = code;
        addAliases(aliases);
    }

    public JiraUser(String team, String name, String code, List<String> aliases) {
        this.team = team;
        this.name = name;
        this.code = code;
        addAliases(aliases);
    }

    public String getTeam() {
        return team;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    private void addAliases(String[] aliases) {
        if (!ArrayUtils.isEmpty(aliases)) {
            this.aliases.addAll(Arrays.asList(aliases));
        }
    }

    private void addAliases(List<String> aliases) {
        if (aliases != null && aliases.size() > 0) {
            this.aliases.addAll(aliases);
        }
    }
}
