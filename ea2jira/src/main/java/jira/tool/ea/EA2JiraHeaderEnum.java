package jira.tool.ea;

import dbtools.common.utils.DateUtils;
import ea.tool.api.EAHeaderEnum;

import java.util.*;

public enum EA2JiraHeaderEnum {
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
    EAGUID(16, "EA-GUID"), // GUID
    Importance(17, "重要性"),

    SubProject(20, "所属子项目"),
    EpicLink(19, "Epic Link"),
    EpicName(18, "Epic Name");

    // Map the Jira header to EA header
    public static Map<EA2JiraHeaderEnum, EAHeaderEnum> JiraEAHeaderMap = new HashMap<EA2JiraHeaderEnum, EAHeaderEnum>() {{
        put(EA2JiraHeaderEnum.Title, EAHeaderEnum.Name);
        put(EA2JiraHeaderEnum.Developer, EAHeaderEnum.Dev);
        put(EA2JiraHeaderEnum.Owner, EAHeaderEnum.Dev);
        put(EA2JiraHeaderEnum.PM, EAHeaderEnum.Author);
        put(EA2JiraHeaderEnum.Estimation, EAHeaderEnum.Estimation);
        put(EA2JiraHeaderEnum.DueDate, EAHeaderEnum.DueDate);
        put(EA2JiraHeaderEnum.QA, null); // guoguilin
        put(EA2JiraHeaderEnum.Project, EAHeaderEnum.ParentKey);
        put(EA2JiraHeaderEnum.SubProject, null);
        put(EA2JiraHeaderEnum.RequirementType, null); // 产品化
        put(EA2JiraHeaderEnum.ProductDate, null); // now
        put(EA2JiraHeaderEnum.RequirementDate, null); // now
        put(EA2JiraHeaderEnum.QAStartDate, EAHeaderEnum.DueDate); // DueDate - 2
        put(EA2JiraHeaderEnum.QAFinishDate, EAHeaderEnum.DueDate); // DueDate
        put(EA2JiraHeaderEnum.EAGUID, EAHeaderEnum.GUID);
        put(EA2JiraHeaderEnum.Label, null);
        put(EA2JiraHeaderEnum.Importance, null);
        put(EA2JiraHeaderEnum.Description, EAHeaderEnum.Notes);
        put(EA2JiraHeaderEnum.EpicLink, null);
        put(EA2JiraHeaderEnum.EpicName, EAHeaderEnum.Keywords);
    }};

    // Some special values which are not from ea
    public static Map<EA2JiraHeaderEnum, String> JiraHeaderValueMap = new HashMap<EA2JiraHeaderEnum, String>() {{
        put(EA2JiraHeaderEnum.QA, "guoguilin"); // guoguilin
        put(EA2JiraHeaderEnum.RequirementType, "产品化"); // 产品化
        put(EA2JiraHeaderEnum.Label, "PMO-EA导入（禁止私动）");
        put(EA2JiraHeaderEnum.Importance, "100");
        put(EA2JiraHeaderEnum.ProductDate, DateUtils.format(new Date(), EA2Jira.Jira_Date_Format)); // now
        put(EA2JiraHeaderEnum.RequirementDate, DateUtils.format(new Date(), EA2Jira.Jira_Date_Format)); // now
    }};

    /**
     * Return the saved headers, sorted by index
     * @return
     */
    public static EA2JiraHeaderEnum[] getSavedHeaders() {
        Set<EA2JiraHeaderEnum> headerSet = JiraEAHeaderMap.keySet();

        EA2JiraHeaderEnum[] headers = new EA2JiraHeaderEnum[headerSet.size()];
        headerSet.toArray(headers);
        Arrays.sort(headers, new Comparator<EA2JiraHeaderEnum>() {
            public int compare(EA2JiraHeaderEnum o1, EA2JiraHeaderEnum o2) {
                return o1.index - o2.index;
            }
        });

        // Update the index
        int index = 0;
        for (EA2JiraHeaderEnum header : headers) {
            header.index = index++;
        }
        return headers;
    }

    private int index;
    private String code;

    EA2JiraHeaderEnum(int index, String code) {
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
