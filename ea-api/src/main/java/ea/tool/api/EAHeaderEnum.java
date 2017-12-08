package ea.tool.api;

import java.util.ArrayList;
import java.util.List;

public enum EAHeaderEnum {
    FileName("File", -1),
    GUID("GUID", 0),
    Type("Type", 1),
    ParentPath("Parent Path", -1),
    Name("Name", 2),

    Author("Author", 3),
    Estimation("Estimation", 4), // Estimation is from Version
    DueDate("Due Date", 5), // Due Date is from Phase
    Dev("Dev", 6), // Dev is from Alias
    QA("QA", -1), // QA is parsed from Notes

    JiraIssueKey("Jira Issue Key", 7), // Jira Issue Key is from Stereotype
    Status("Status", 8),
    Notes("Notes", 9),

    Keywords("Keywords", -1),
    CreatedDate("Created Date", -1),
    CreatedTime("Created Time", -1),
    ModifiedDate("Modified Date", -1),
    ModifiedTime("Modified Time", -1),

    Key("CSV_KEY", 10),
    ParentKey("CSV_PARENT_KEY", 11);

    // The needed headers from csv file
    private static EAHeaderEnum[] headerList = new EAHeaderEnum[]{
            FileName, GUID, Type, ParentPath, Name,
            Author, Estimation, DueDate, Dev, QA,
            JiraIssueKey, Status, Notes,
            Keywords, CreatedDate, CreatedTime, ModifiedDate, ModifiedTime,
            Key, ParentKey,
    };

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
            header.setIndex(index);

            if (index < 0 || index >= strHeaders.size()) {
                System.out.printf("Can't find header: %s, %s\n", header.getCode(), strHeaders.toString());
            }
        }
    }

    private String code;
    private int index;

    EAHeaderEnum(String code, int index) {
        this.code = code;
        this.index = index;
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
}
