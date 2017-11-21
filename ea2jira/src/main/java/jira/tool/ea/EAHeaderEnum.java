package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;

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
    Key("CSV_KEY", 10),
    ParentKey("CSV_PARENT_KEY", 11);

    private static EAHeaderEnum[] list = new EAHeaderEnum[]{
            GUID, Type, Name, Author, Estimation, DueDate, Owner, JiraIssueKey, Status, Notes, Key, ParentKey,
    };

    // Fill the index
    public static void fillIndex(String[] headers) {
        if (ArrayUtils.isEmpty(headers)) {
            return;
        }

        // Get the list of headers
        List<String> strHeaders = new ArrayList<String>(headers.length);
        for (String header : headers) {
            strHeaders.add(header.toLowerCase());
        }

        // Find the index
        for (EAHeaderEnum header : list) {
            header.setIndex(strHeaders.indexOf(header.getCode().toLowerCase()));
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
