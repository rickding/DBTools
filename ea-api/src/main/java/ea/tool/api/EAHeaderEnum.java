package ea.tool.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public enum EAHeaderEnum {
//    FileName("File", -1),
//    GUID("GUID", 0),
//    Type("Type", 1),
//    ParentPath("Parent Path", -1),
//    Name("Name", 2),
//
//    Author("Author", 3),
//    Estimation("Estimation", 4, new String[]{"Version"}), // Estimation is from Version
//    DueDate("Due Date", 5, new String[]{"Phase"}), // Due Date is from Phase
//    Dev("Dev", 6, new String[]{"Alias"}), // Dev is from Alias
//    QA("QA", -1), // QA is parsed from Notes
//
//    JiraIssueKey("Jira Issue Key", 7, new String[]{"Stereotype"}), // Jira Issue Key is from Stereotype
//    Status("Status", 8),
//    Notes("Notes", 9),
//
//    Keywords("Keywords", -1),
//    CreatedDate("Created Date", -1),
//    CreatedTime("Created Time", -1),
//    ModifiedDate("Modified Date", -1),
//    ModifiedTime("Modified Time", -1),

    FileName("文件", -1),
    GUID("GUID", 0),
    Type("Type", 1),
    ParentPath("需求项路径", -1),
    Name("需求项", 2),

    Author("提出人", 3),
    Estimation("估时", 4, new String[]{"Version"}), // Estimation is from Version
    DueDate("上线日期", 5, new String[]{"Phase"}), // Due Date is from Phase
    Dev("开发", 6, new String[]{"Alias"}), // Dev is from Alias
    QA("测试", -1), // QA is parsed from Notes
    DevRemark("开发备注", -1), //
    QARemark("测试备注", -1), //

    JiraIssueKey("JIRA ID", 7, new String[]{"Stereotype"}), // Jira Issue Key is from Stereotype
    Status("状态", 8),
    Notes("备注", 9),

    Keywords("Keywords", -1),
    CreatedDate("创建日期", -1),
    CreatedTime("Created Time", -1),
    ModifiedDate("更新日期", -1),
    ModifiedTime("Modified Time", -1),

    Key("CSV_KEY", 10),
    ParentKey("CSV_PARENT_KEY", 11);

    // The needed headers from csv file
//    private static EAHeaderEnum[] headerList = new EAHeaderEnum[]{
//            FileName, GUID, Type, ParentPath, Name,
//            Author, Estimation, DueDate, Dev, QA,
//            JiraIssueKey, Status, Notes,
//            Keywords, CreatedDate, CreatedTime, ModifiedDate, ModifiedTime,
//            Key, ParentKey,
//    };

    private static EAHeaderEnum[] headerList = new EAHeaderEnum[]{
            FileName, ParentPath, Name,
            Author, DueDate, Dev, QA,
            Status, DevRemark, QARemark,
            JiraIssueKey, Notes,GUID,
            Estimation, CreatedDate, ModifiedDate,
            CreatedTime, ModifiedTime, Keywords, Key, ParentKey, Type,
    };

    private static Set<EAHeaderEnum> generatedHeaderSet = new HashSet<EAHeaderEnum>() {{
       add(FileName);
       add(ParentPath);
       add(QA);
       add(CreatedTime);
       add(ModifiedTime);
    }};

    public static String[] getHeaders() {
        String[] headers = new String[headerList.length];
        for (int i = 0; i < headerList.length; i++) {
            headers[i] = headerList[i].code;
        }
        return headers;
    }

    /**
     * Fill the index according to csv file headers
     * @param headers
     */
    public static void fillIndex(String[] headers) {
        if (headers == null || headers.length <= 0) {
            return;
        }

        // Get the list of headers
        List<String> strHeaders = new ArrayList<String>(headers.length);
        for (String header : headers) {
            strHeaders.add(header.toLowerCase());
        }

        // Find the index
        for (EAHeaderEnum header : headerList) {
            int index = strHeaders.indexOf(header.getCode().toLowerCase());
            if (index < 0 && header.aliases != null && header.aliases.length > 0) {
                for (String alias : header.getAliases()) {
                    index = strHeaders.indexOf(alias.toLowerCase());
                    if (index > 0) {
                        break;
                    }
                }
            }
            header.setIndex(index);

            if ((index < 0 || index >= strHeaders.size()) && !generatedHeaderSet.contains(header)) {
                System.out.printf("Can't find header: %s, %s\n", header.getCode(), strHeaders.toString());
            }
        }
    }

    private String code;
    private int index;
    private String[] aliases;

    EAHeaderEnum(String code, int index) {
        this.code = code;
        this.index = index;
    }

    EAHeaderEnum(String code, int index, String[] aliases) {
        this.code = code;
        this.index = index;
        this.aliases = aliases;
    }

    public String getCode() {
        return code;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String[] getAliases() {
        return aliases;
    }
}
