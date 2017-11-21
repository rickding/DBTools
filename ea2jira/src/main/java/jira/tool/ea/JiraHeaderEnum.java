package jira.tool.ea;

import dbtools.common.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public enum JiraHeaderEnum {
    Title("主题"),
    Developer("经办人"), // Alias
    Owner("开发负责人"), // Alias
    PM("产品负责人"), // Author
    Creator("报告人"), // Author or the creator
    Estimation("预估时间"), // Estimation
    DueDate("到期日"), // DueDate
    QA("测试负责人"), // guoguilin
    Project("所属项目"), // Package (Parent key)
    RequirementType("需求类型"), // 产品化
    ProductDate("产品设计完成时间"), // Now
    RequirementDate("需求提出时间"), // Now
    QAStartDate("计划送测日期"), // DueDate - 2
    QAFinishDate("测试完成日期"), // DueDate
    EAGUID("EA-GUID"), // GUID
    Description("描述"); // Notes

    public static Map<JiraHeaderEnum, EAHeaderEnum> JiraEAHeaderMap = new HashMap<JiraHeaderEnum, EAHeaderEnum>() {{
        put(JiraHeaderEnum.Title, EAHeaderEnum.Name);
        put(JiraHeaderEnum.Developer, EAHeaderEnum.Owner);
        put(JiraHeaderEnum.Owner, EAHeaderEnum.Owner);
        put(JiraHeaderEnum.PM, EAHeaderEnum.Author);
        put(JiraHeaderEnum.Creator, EAHeaderEnum.Author);
        put(JiraHeaderEnum.Estimation, EAHeaderEnum.Estimation);
        put(JiraHeaderEnum.DueDate, EAHeaderEnum.DueDate);
        put(JiraHeaderEnum.QA, null); // guoguilin
        put(JiraHeaderEnum.Project, EAHeaderEnum.ParentKey);
        put(JiraHeaderEnum.RequirementType, null); // 产品化
        put(JiraHeaderEnum.ProductDate, null); // now
        put(JiraHeaderEnum.RequirementDate, null); // now
        put(JiraHeaderEnum.QAStartDate, EAHeaderEnum.DueDate); // DueDate - 2
        put(JiraHeaderEnum.QAFinishDate, EAHeaderEnum.DueDate); // DueDate
        put(JiraHeaderEnum.EAGUID, EAHeaderEnum.GUID);
        put(JiraHeaderEnum.Description, EAHeaderEnum.Notes);
    }};

    public static Map<JiraHeaderEnum, String> JiraHeaderValueMap = new HashMap<JiraHeaderEnum, String>() {{
        put(JiraHeaderEnum.QA, "guoguilin"); // guoguilin
        put(JiraHeaderEnum.RequirementType, "产品化"); // 产品化
        put(JiraHeaderEnum.ProductDate, DateUtils.format(new Date(), "yyyy-MM-dd")); // now
        put(JiraHeaderEnum.RequirementDate, DateUtils.format(new Date(), "yyyy-MM-dd")); // now
    }};

    private String code;

    JiraHeaderEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
