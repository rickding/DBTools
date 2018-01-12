package jira.tool.db.model;

import dbtools.common.utils.StrUtils;
import org.apache.ibatis.type.Alias;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Alias("User")
public class User {
    private long id;
    private String code;
    private String name;
    private String department;
    private String team;
    private String entryDate;
    private String leaveDate;
    private String isActive;
    private String unserInvestigate;
    private String devPhase;
    private String isHead;

    private boolean active;
    private String userName;
    private String lowerUserName;
    private String firstName;
    private String lowerFirstName;
    private String lastName;
    private String lowerLastName;
    private String displayName;
    private String lowerDisplayName;

    private int memberCount;

    public String[] getAliases() {
        Set<String> aliasSet = new HashSet<String>() {{
            add(code);
            add(name);
            add(userName);
            add(lowerUserName);
            add(firstName);
            add(lowerFirstName);
            add(lastName);
            add(lowerLastName);
            add(displayName);
            add(lowerDisplayName);
        }};

        List<String> aliasList = new ArrayList<String>(aliasSet.size());
        for(String alias : aliasSet) {
            if (!StrUtils.isEmpty(alias)) {
                aliasList.add(alias.trim());
            }
        }

        String[] aliasArr = new String[aliasList.size()];
        aliasList.toArray(aliasArr);
        return aliasArr;
    }

    public int getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    public long getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDepartment() {
        return department;
    }

    public String getTeam() {
        return team;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getLeaveDate() {
        return leaveDate;
    }

    public String getIsActive() {
        return isActive;
    }

    public String getUnserInvestigate() {
        return unserInvestigate;
    }

    public String getDevPhase() {
        return devPhase;
    }

    public String getIsHead() {
        return isHead;
    }

    public boolean isActive() {
        return active;
    }

    public String getUserName() {
        return userName;
    }

    public String getLowerUserName() {
        return lowerUserName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLowerFirstName() {
        return lowerFirstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLowerLastName() {
        return lowerLastName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getLowerDisplayName() {
        return lowerDisplayName;
    }
}
