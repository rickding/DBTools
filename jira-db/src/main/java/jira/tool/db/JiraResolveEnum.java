package jira.tool.db;

public enum JiraResolveEnum {
    Canceled("取消"),
    Finished("完成", new String[]{"Done"}),
    Resolved("已解决");

    private String code;
    private String[] aliases;

    JiraResolveEnum(String code) {
        this.code = code;
    }

    JiraResolveEnum(String code, String[] aliases) {
        this.code = code;
        this.aliases = aliases;
    }

    public String getCode() {
        return code;
    }

    public String[] getAliases() {
        return aliases;
    }
}
