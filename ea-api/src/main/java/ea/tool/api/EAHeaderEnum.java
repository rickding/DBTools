package ea.tool.api;

import java.util.ArrayList;
import java.util.List;

public enum EAHeaderEnum {
    GUID("GUID", 0),
    Type("Type", 1),
    Name("Name", 2),

    Author("Author", 3),
    Estimation("Version", 4),
    DueDate("Phase", 5),
    Owner("Alias", 6),

    JiraIssueKey("Stereotype", 7),
    Status("Status", 8),
    Notes("Notes", 9),

    Keywords("Keywords", -1),
    CreatedDate("Created Date", -1),
    ModifiedDate("Modified Date", -1),

    Key("CSV_KEY", 10),
    ParentKey("CSV_PARENT_KEY", 11);

    // The needed headers from csv file
    private static EAHeaderEnum[] headerList = new EAHeaderEnum[]{
            GUID, Type, Name,
            Author, Estimation, DueDate, Owner,
            JiraIssueKey, Status, Notes,
            Keywords, CreatedDate, ModifiedDate,
            Key, ParentKey,
    };

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
