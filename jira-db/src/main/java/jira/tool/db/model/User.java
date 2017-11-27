package jira.tool.db.model;

import org.apache.ibatis.type.Alias;

@Alias("User")
public class User {
    private int id;
    private String code;
    private String name;
    private String department;
    private String productLine;
    private String entryDate;
    private String leaveDate;
    private String isActive;
    private String unserInvestigate;
    private String devPhase;
    private String isHead;
}
