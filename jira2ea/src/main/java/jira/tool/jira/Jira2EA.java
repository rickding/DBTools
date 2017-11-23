package jira.tool.jira;

import dbtools.common.file.CsvUtil;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.util.*;

public class Jira2EA {
    private static String Jira_Header_GUID = "Custom field (EA-GUID)";
    private static String Jira_Header_Key = "Issue key";
    private static String Jira_Issue_Id = "Issue id";

    private static String EA_Header_GUID = "GUID";
    private static String EA_Header_Key = "Stereotype";
    private static String EA_Header_Name = "Name";

    private static String EA_Header_Type = "Type";
    private static List<String> EA_Type_Mapped = new ArrayList<String>() {{
        add("Requirement");
    }};
    private static List<String> EA_Type_Package = new ArrayList<String>() {{
        add("Package");
    }};

    private static List<String> EA_Type_Saved = new ArrayList<String>() {{
        add("Package");
        add("Requirement");
    }};

    private static String[] EA_Value_Saved = new String[] {"GUID", "Type", "Stereotype", "CSV_KEY", "CSV_PARENT_KEY"};

    public static List<String[]> getSavedValues(List<String[]> elements) {
        if (elements == null || elements.size() <= 0 || EA_Value_Saved.length <= 0) {
            return null;
        }

        // Check headers firstly
        final String[] headers = elements.get(0);
        if (ArrayUtils.isEmpty(headers)) {
            return null;
        }

        int typeIndex = getHeaderIndex(EA_Header_Type, headers);
        if (typeIndex < 0) {
            System.out.printf("Can't find the headers: %s\n", headers.toString());
            return null;
        }

        // Header index
        int[] headerIndexArr = new int[EA_Value_Saved.length];
        for (int i = 0; i < headerIndexArr.length; i++) {
            String header = EA_Value_Saved[i];
            int index = getHeaderIndex(header, headers);
            if (index < 0 || index >= headers.length) {
                System.out.printf("Header: %s, list: %s\n", header, headers.toString());
            }

            headerIndexArr[i] = index;
        }

        // Data
        List<String[]> records = new ArrayList<String[]>(elements.size()){{
            add(EA_Value_Saved);
        }};

        for (String[] element : elements) {
            String type = element[typeIndex];
            if (StrUtils.isEmpty(type) || !EA_Type_Saved.contains(type)) {
                continue;
            }

            int i = 0;
            String[] values = new String[headerIndexArr.length];
            for (int j : headerIndexArr) {
                values[i++] = element[j];
            }
            records.add(values);
        }
        return records;
    }

    public static List<String[]> updateStoryToElement(List<String[]> elements, Map<String, String> guidStoryMap, Map<String, String> noGUIDFromJiraMap) {
        if (elements == null || elements.size() <= 1 || guidStoryMap == null || guidStoryMap.size() <= 0) {
            return null;
        }

        // Check headers firstly
        int i = 0;
        final String[] headers = elements.get(i++);
        if (ArrayUtils.isEmpty(headers)) {
            return null;
        }

        int guidIndex = getHeaderIndex(EA_Header_GUID, headers);
        int keyIndex = getHeaderIndex(EA_Header_Key, headers);
        int nameIndex = getHeaderIndex(EA_Header_Name, headers);
        int typeIndex = getHeaderIndex(EA_Header_Type, headers);

        if (guidIndex < 0 || keyIndex < 0 || typeIndex < 0) {
            System.out.printf("Can't find the headers: %s\n", headers.toString());
            return null;
        }

        // data
        List<String[]> newElements = new ArrayList<String[]>(){{
            add(headers);
        }};

        if (noGUIDFromJiraMap != null) {
            noGUIDFromJiraMap.clear();
        }

        boolean newMpped = false;
        for (; i < elements.size(); i++) {
            String[] values = elements.get(i);
            if (ArrayUtils.isEmpty(values) || guidIndex >= values.length || keyIndex >= values.length || typeIndex >= values.length) {
                continue;
            }

            String type = values[typeIndex];
            if (StrUtils.isEmpty(type)) {
                continue;
            }
            if (EA_Type_Package.contains(type)) {
                newElements.add(values);
                continue;
            }
            if (!EA_Type_Mapped.contains(type)) {
                continue;
            }

            String guid = values[guidIndex];
            if (StrUtils.isEmpty(guid)) {
                continue;
            }

            String name = nameIndex >= 0 && nameIndex < values.length ? values[nameIndex] : null;
            String story = values[keyIndex];
            String newStory = guidStoryMap.get(guid);
            boolean hasStory = !StrUtils.isEmpty(story);
            boolean hasNewStory = !StrUtils.isEmpty(newStory);

            if (!hasStory && !hasNewStory) {
                // Not connected
                System.out.printf("GUID doesn't connect with story, while still no one from Jira: %s, %s\n", guid, name);
            } else if (!hasStory && hasNewStory) {
                // New story
                values[keyIndex] = newStory;
                newElements.add(values);
                newMpped = true;
            } else if (hasStory && !hasNewStory) {
                // Old story but no new story
                System.out.printf("GUID connects with one story, but no GUID from Jira story: %s, %s, %s\n", guid, story, name);
                if (noGUIDFromJiraMap != null) {
                    if (noGUIDFromJiraMap.containsValue(story)) {
                        System.out.printf("GUIDs connect with same story: %s, %s, %s\n", guid, story, name);
                    }
                    noGUIDFromJiraMap.put(guid, story);
                }
            } else if (hasStory && hasNewStory && !story.equalsIgnoreCase(newStory)) {
                System.out.printf("GUID connects with multiple stories: %s, %s, %s, %s\n", guid, story, newStory, name);
            }
        }

        if (!newMpped) {
            newElements.clear();
        }
        return newElements;
    }

    public static String[] generateUpdateJiraGUIDSSQL(Map<String, String> updateJiraGuidMap, Map<String, String> issueKeyIdMap) {
        if (updateJiraGuidMap == null || updateJiraGuidMap.size() <= 0 || issueKeyIdMap == null || issueKeyIdMap.size() <= 0) {
            return null;
        }

        List<String> sqlList = new ArrayList<String>(updateJiraGuidMap.size());
        sqlList.add("set @valueId = (select IFNULL(max(id), 0) from jiradb.customfieldvalue where id < 10424);");
        String customFieldId = "select id from jiradb.customfield where cfname = 'EA-GUID'";

        List<String> issueIds = new ArrayList<String>();
        for (Map.Entry<String, String> jiraGuid : updateJiraGuidMap.entrySet()) {
            String issueId = issueKeyIdMap.get(jiraGuid.getValue());
            if (StrUtils.isEmpty(issueId) || StrUtils.isEmpty(jiraGuid.getKey())) {
                continue;
            }

            sqlList.add(String.format(
                    "insert into jiradb.customfieldvalue(id, issue, CUSTOMFIELD, stringvalue) values(%s, %s, (%s), '%s');",
                    "@valueId := @valueId + 1",
                    issueId,
                    customFieldId,
                    jiraGuid.getKey()
            ));

            issueIds.add(issueId);
        }

        if (issueIds != null && issueIds.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (String issueId : issueIds) {
                sb.append(",");
                sb.append(issueId);
            }

            sqlList.add(0, String.format(
                    "delete from jiradb.customfieldvalue where issue in (%s) and CUSTOMFIELD in (%s);",
                    sb.toString().substring(1),
                    customFieldId
            ));
        }

        String[] values = new String[sqlList.size()];
        sqlList.toArray(values);
        return values;
    }

    private static int getHeaderIndex(String header, String[] headerArray) {
        if (StrUtils.isEmpty(header) || headerArray == null || headerArray.length <= 0) {
            return -1;
        }

        int i = 0;
        for (String h : headerArray) {
            i++;
            if (StrUtils.isEmpty(h)) {
                continue;
            }

            String tmp = h.trim();
            if (StrUtils.isEmpty(tmp)) {
                continue;
            }

            if (tmp.equalsIgnoreCase(header) || tmp.toLowerCase().endsWith(header.toLowerCase())) {
                return i - 1;
            }
        }

        return -1;
    }

    public static Map<String, String> getGUIDStoryMap(String csvFile, Map<String, String> issueKeyIdMap) {
        List<String[]> storyList = CsvUtil.readFile(csvFile);
        if (storyList == null || storyList.size() <= 1) {
            return null;
        }

        // Check headers firstly
        int i = 0;
        String[] headers = storyList.get(i++);
        if (ArrayUtils.isEmpty(headers)) {
            return null;
        }

        List<String> headerList = Arrays.asList(headers);
        int guidIndex = headerList.indexOf(Jira_Header_GUID);
        int keyIndex = headerList.indexOf(Jira_Header_Key);
        int idIndex = headerList.indexOf(Jira_Issue_Id);

        if (guidIndex < 0 || keyIndex < 0 || idIndex < 0) {
            System.out.printf("Can't find the headers: %s\n", headerList.toString());
            return null;
        }

        // Walk through the data
        Map<String, String> guidStoryMap = new HashMap<String, String>();
        for (; i < storyList.size(); i++) {
            String[] values = storyList.get(i);
            if (ArrayUtils.isEmpty(values) || guidIndex >= values.length || keyIndex >= values.length || idIndex >= values.length) {
                continue;
            }

            String story = values[keyIndex];
            String id = values[idIndex];
            if (StrUtils.isEmpty(story) || StrUtils.isEmpty(id)) {
                continue;
            }
            issueKeyIdMap.put(story, id);

            String guid = values[guidIndex];
            if (StrUtils.isEmpty(guid)) {
                continue;
            }

            if (guidStoryMap.containsKey(guid)) {
                System.out.printf("GUID connects with multiple stories: %s, %s, %s\n", guid, guidStoryMap.get(guid), story);
            } else {
                guidStoryMap.put(guid, story);
            }
        }

        return guidStoryMap;
    }
}
