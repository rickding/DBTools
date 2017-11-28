package jira.tool.db.model;

import org.apache.ibatis.type.Alias;

@Alias("User")
public class User {
    private int id;
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

    public int getId() {
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
}
