package jira.tool.ea;

public enum JiraStatusEnum {
    Closed("关闭");

    private String code;

    JiraStatusEnum(String code) {
        this.code = code;
    }
}
