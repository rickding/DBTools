package jira.tool.db;

import dbtools.common.utils.ArrayUtils;
import jira.tool.db.model.Story;

import java.util.ArrayList;
import java.util.List;

public enum JiraHeaderEnum {
    Title("主题"),
    Key("Issue key"),
    ID("Issue id"),
    Status("状态"),
    Resolve("解决结果"),
    Label("标签"),
    EAGUID("Custom field (EA-GUID)");

    // The needed headers from csv file
    private static JiraHeaderEnum[] list = new JiraHeaderEnum[]{
            Title, Key, ID, Status, Resolve, Label, EAGUID,
    };

    public static List<String[]> formatStoryList(List<Story> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        List<String[]> storyList = new ArrayList<String[]>() {{
            add(getHeaders());
        }};

        for (Story story : list) {
            storyList.add(formatStory(story));
        }
        return storyList;
    }

    private static String[] formatStory(Story story) {
        if (story == null) {
            return null;
        }

        return new String[] {
                story.getTitle(), story.getKey(), String.valueOf(story.getId()),
                story.getStatus(), story.getResolution(),
                story.getLabel(), story.getEAGUID()
        };
    }

    private static String[] getHeaders() {
        String[] headers = new String[list.length];
        for (int i = 0; i < list.length; i++) {
            headers[i] = list[i].code;
        }
        return headers;
    }

    /**
     * Fill the index according to csv file headers. Return the label indexes.
     * @param headers
     */
    public static List<Integer> fillIndex(String[] headers) {
        if (ArrayUtils.isEmpty(headers)) {
            return null;
        }

        // Get the list of headers
        List<String> strHeaders = new ArrayList<String>(headers.length);
        for (String header : headers) {
            strHeaders.add(header.toLowerCase());
        }

        // Find the index
        for (JiraHeaderEnum header : list) {
            int index = strHeaders.indexOf(header.getCode().toLowerCase());
            header.setIndex(index);

            if (index < 0 || index >= strHeaders.size()) {
                System.out.printf("Can't find header: %s, %s\n", header.getCode(), strHeaders.toString());
            }
        }

        // Find the label index list
        List<Integer> labelIndexes = new ArrayList<Integer>();
        for (int headerIndex = 0; headerIndex < headers.length; headerIndex++) {
            if (Label.code.equalsIgnoreCase(headers[headerIndex])) {
                labelIndexes.add(headerIndex);
            }
        }

        return labelIndexes;
    }

    private String code;
    private int index;

    JiraHeaderEnum(String code) {
        this.code = code;
        this.index = -1;
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
