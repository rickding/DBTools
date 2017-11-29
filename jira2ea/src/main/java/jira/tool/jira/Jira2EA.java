package jira.tool.jira;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.ea.*;

import java.util.*;

public class Jira2EA {
    private static String[] EA_Value_Saved = new String[] {"GUID", "Type", "Stereotype", "Status", "CSV_KEY", "CSV_PARENT_KEY"};
    private static List<String> EA_Type_Saved = new ArrayList<String>() {{
        add("Package");
        add("Requirement");
    }};

    public static List<String[]> getSavedValues(List<String[]> elements) {
        if (elements == null || elements.size() <= 0 || EA_Value_Saved.length <= 0) {
            return null;
        }

        // Check headers firstly
        final String[] headers = elements.get(0);
        if (ArrayUtils.isEmpty(headers)) {
            return null;
        }

        // Header index
        int[] headerIndexArr = new int[EA_Value_Saved.length];
        for (int i = 0; i < headerIndexArr.length; i++) {
            String header = EA_Value_Saved[i];
            int index = getHeaderIndex(header, headers);
            if (index < 0 || index >= headers.length) {
                System.out.printf("Can't find header: %s, list: %s\n", header, headers.toString());
            }
            headerIndexArr[i] = index;
        }

        // Data
        List<String[]> records = new ArrayList<String[]>(elements.size()){{
            add(EA_Value_Saved);
        }};
        for (String[] element : elements) {
            String type = element[EAHeaderEnum.Type.getIndex()];
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

    public static List<String[]> updateStoryKeyToElement(List<String[]> elements, Map<String, String> guidStoryKeyMap, Map<String, String> noGUIDFromJiraMap) {
        if (elements == null || elements.size() <= 1 || guidStoryKeyMap == null || guidStoryKeyMap.size() <= 0) {
            return null;
        }

        // Check headers firstly
        int elementIndex = 0;
        final String[] headers = elements.get(elementIndex++);
        if (ArrayUtils.isEmpty(headers)) {
            return null;
        }
        EAHeaderEnum.fillIndex(headers);

        // data
        List<String[]> newElements = new ArrayList<String[]>();
        List<String> noStoryFromEA = new ArrayList<String>();
        List<String> noGUIDFromJira = new ArrayList<String>();

        for (; elementIndex < elements.size(); elementIndex++) {
            String[] element = elements.get(elementIndex);
            if (ArrayUtils.isEmpty(element)) {
                continue;
            }

            // Only process implemented requirement as story
            if (!EATypeEnum.isMappedToStory(element[EAHeaderEnum.Type.getIndex()])
                    || !EAStatusEnum.isMappedToStory(element[EAHeaderEnum.Status.getIndex()])) {
                continue;
            }

            // TODO: Skip the old issues currently.
            if (EADateUtil.Date_Skip.compareTo(EADateUtil.format(element[EAHeaderEnum.CreatedDate.getIndex()])) > 0
                    && EADateUtil.Date_Skip.compareTo(EADateUtil.format(element[EAHeaderEnum.ModifiedDate.getIndex()])) > 0) {
                continue;
            }

            // Check guid
            String guid = element[EAHeaderEnum.GUID.getIndex()];
            if (StrUtils.isEmpty(guid)) {
                continue;
            }

            // Check story key
            String name = element[EAHeaderEnum.Name.getIndex()];
            String story = element[EAHeaderEnum.JiraIssueKey.getIndex()];
            String newStory = guidStoryKeyMap.get(guid);
            boolean hasStory = JiraKeyUtil.isValid(story);
            boolean hasNewStory = JiraKeyUtil.isValid(newStory);

            if (!hasStory && !hasNewStory) {
                // Not connected
                noStoryFromEA.add(String.format("GUID doesn't connect with story, while still no one from Jira: %s, %s\n", guid, name));
            } else if (!hasStory && hasNewStory) {
                // New story
                element[EAHeaderEnum.JiraIssueKey.getIndex()] = newStory;
                newElements.add(element);
            } else if (hasStory && !hasNewStory) {
                // Old story but no new story
                noGUIDFromJira.add(String.format("GUID connects with one story, but no GUID from Jira story: %s, %s, %s\n", guid, story, name));
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

        // Info
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        int infoIndex = 0;
        for (String str : noGUIDFromJira) {
            sb.append(String.format("%d, %s", infoIndex++, str));
        }

        sb.append("\n");
        infoIndex = 0;
        for (String str : noStoryFromEA) {
            sb.append(String.format("%d, %s", infoIndex++, str));
        }
        System.out.println(sb.toString());

        if (newElements.size() > 0) {
            newElements.add(0, headers);

            // Add the parent elements
            addParentElements(newElements, elements);
        }
        return newElements;
    }

    private static void addParentElements(List<String[]> childrenElements, List<String[]> parentElements) {
        if (childrenElements == null || childrenElements.size() <= 1 || parentElements == null || parentElements.size() <= 1) {
            return;
        }

        // Both elements list have headers at first line
        int startIndex = 1;

        // Find the parent key, map key to children elements
        Set<String> parentKeySet = new HashSet<String>(childrenElements.size());
        Set<String> keySet = new HashSet<String>(childrenElements.size());

        for (int i = startIndex; i < childrenElements.size(); i++) {
            String[] element = childrenElements.get(i);
            String key = element[EAHeaderEnum.ParentKey.getIndex()];
            if (!StrUtils.isEmpty(key)) {
                parentKeySet.add(key);
            }

            key = element[EAHeaderEnum.Key.getIndex()];
            if (!StrUtils.isEmpty(key)) {
                keySet.add(key);
            }
        }

        // Remove the existed parent elements
        parentKeySet.removeAll(keySet);
        if (parentKeySet.size() <= 0) {
            return;
        }

        // Generate the map: key to parent elements
        int keyIndex = EAHeaderEnum.Key.getIndex();
        Map<String, String[]> mapParentKeyElements = new HashMap<String, String[]>(parentElements.size());
        for (int i = startIndex; i < parentElements.size(); i++) {
            String[] element = parentElements.get(i);
            if (!ArrayUtils.isEmpty(element) && keyIndex >= 0 && keyIndex < element.length) {
                String key = element[keyIndex];
                if (!StrUtils.isEmpty(key)) {
                    mapParentKeyElements.put(key, element);
                }
            }
        }

        // Loop to find the parent elements
        for (String key : parentKeySet) {
            while (!keySet.contains(key)) {
                if (!mapParentKeyElements.containsKey(key)) {
                    System.out.printf("Error when find parent element: %s", key);
                    break;
                }

                String[] parentElement = mapParentKeyElements.get(key);
                childrenElements.add(startIndex, parentElement);
                keySet.add(key);

                key = parentElement[EAHeaderEnum.ParentKey.getIndex()];
                if (StrUtils.isEmpty(key)) {
                    break;
                }
            }
        }
    }

    public static String[] generateUpdateJiraGUIDSQL(Map<String, String> updateJiraGuidMap, Map<String, String> issueKeyIdMap) {
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
                System.out.printf("Can't find story id for key: %s, %s\n", jiraGuid.getKey(), jiraGuid.getValue());
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
}
