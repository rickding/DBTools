package jira.tool.ea;

import dbtools.common.utils.DateUtils;

import java.util.*;

public enum JiraHeaderEnum {
    Title(1, "主题"),
    Project(2, "所属项目"), // Package (Parent key)
    RequirementType(3, "需求类型"), // 产品化
    Description(4, "描述"), // Notes
    PM(5, "产品负责人"), // Author
    Owner(6, "开发负责人"), // Alias
    QA(7, "测试负责人"), // guoguilin
    Developer(8, "经办人"), // Alias
    Estimation(9, "预估时间"), // Estimation
    RequirementDate(10, "需求提出时间"), // Now
    ProductDate(11, "产品设计完成日期"), // Now
    QAStartDate(12, "计划送测日期"), // DueDate - 2
    QAFinishDate(13, "测试完成日期"), // DueDate
    DueDate(14, "到期日"), // DueDate
    Label(15, "标签"),
    Importance(17, "重要性"),
    EAGUID(16, "EA-GUID"); // GUID

    /**
     * Map the Jira header to EA header
     */
    public static Map<JiraHeaderEnum, EAHeaderEnum> JiraEAHeaderMap = new HashMap<JiraHeaderEnum, EAHeaderEnum>() {{
        put(JiraHeaderEnum.Title, EAHeaderEnum.Name);
        put(JiraHeaderEnum.Developer, EAHeaderEnum.Owner);
        put(JiraHeaderEnum.Owner, EAHeaderEnum.Owner);
        put(JiraHeaderEnum.PM, EAHeaderEnum.Author);
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
        put(JiraHeaderEnum.Label, null);
        put(JiraHeaderEnum.Importance, null);
        put(JiraHeaderEnum.Description, EAHeaderEnum.Notes);
    }};

    // Some special values which are not from ea
    public static Map<JiraHeaderEnum, String> JiraHeaderValueMap = new HashMap<JiraHeaderEnum, String>() {{
        put(JiraHeaderEnum.QA, "guoguilin"); // guoguilin
        put(JiraHeaderEnum.RequirementType, "产品化"); // 产品化
        put(JiraHeaderEnum.Label, "PMO-EA导入（禁止私动）");
        put(JiraHeaderEnum.Importance, "100");
        put(JiraHeaderEnum.ProductDate, DateUtils.format(new Date(), EA2Jira.Jira_Date_Format)); // now
        put(JiraHeaderEnum.RequirementDate, DateUtils.format(new Date(), EA2Jira.Jira_Date_Format)); // now
    }};

    /**
     * Return the saved headers, sorted by index
     * @return
     */
    public static JiraHeaderEnum[] getSortedHeaders() {
        Set<JiraHeaderEnum> headerSet = JiraEAHeaderMap.keySet();

        JiraHeaderEnum[] headers = new JiraHeaderEnum[headerSet.size()];
        headerSet.toArray(headers);
        Arrays.sort(headers, new Comparator<JiraHeaderEnum>() {
            public int compare(JiraHeaderEnum o1, JiraHeaderEnum o2) {
                return o1.index - o2.index;
            }
        });

        int index = 0;
        for (JiraHeaderEnum header : headers) {
            header.index = index++;
        }
        return headers;
    }

    private int index;
    private String code;

    JiraHeaderEnum(int index, String code) {
        this.index = index;
        this.code = code;
    }

    public int getIndex() {
        return index;
    }

    public String getCode() {
        return code;
    }
}
