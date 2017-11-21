package jira.tool.ea;

import dbtools.common.utils.DateUtils;

import java.util.*;

public enum JiraHeaderEnum {
    Project(1, "所属项目"), // Package (Parent key)
    RequirementType(2, "需求类型"), // 产品化
    Title(3, "主题"),
    Creator(4, "报告人"), // Author or the creator
    PM(5, "产品负责人"), // Author
    Owner(6, "开发负责人"), // Alias
    Developer(7, "经办人"), // Alias
    Estimation(8, "预估时间"), // Estimation
    DueDate(9, "到期日"), // DueDate
    QA(10, "测试负责人"), // guoguilin
    ProductDate(11, "产品设计完成时间"), // Now
    RequirementDate(12, "需求提出时间"), // Now
    QAStartDate(13, "计划送测日期"), // DueDate - 2
    QAFinishDate(14, "测试完成日期"), // DueDate
    EAGUID(15, "EA-GUID"), // GUID
    Description(17, "描述"); // Notes

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

    public static JiraHeaderEnum[] getSortedHeaders() {
        Set<JiraHeaderEnum> headerSet = JiraEAHeaderMap.keySet();
        JiraHeaderEnum[] headers = new JiraHeaderEnum[headerSet.size()];
        headerSet.toArray(headers);

        Arrays.sort(headers, new Comparator<JiraHeaderEnum>() {
            public int compare(JiraHeaderEnum o1, JiraHeaderEnum o2) {
                return o1.index - o2.index;
            }
        });
        return headers;
    }

    private int index;
    private String code;

    JiraHeaderEnum(int index, String code) {
        this.index = index;
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
