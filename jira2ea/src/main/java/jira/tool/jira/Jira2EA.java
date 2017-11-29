package jira.tool.jira;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.ea.*;

import java.util.*;

public class Jira2EA {
    private static String[] EA_Value_Saved = new String[]{"GUID", "Type", "Stereotype", "Status", "CSV_KEY", "CSV_PARENT_KEY"};
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
        List<String[]> records = new ArrayList<String[]>(elements.size()) {{
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

    public static List<String[]> updateStoryInfoIntoElement(
            List<String[]> elements, Map<String, String> guidKeyMap, Map<String, String[]> keyStoryMap,
            Map<String, String> noGUIDFromJiraMap
    ) {
        if (elements == null || elements.size() <= 1 || guidKeyMap == null || guidKeyMap.size() <= 0) {
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
        List<String[]> newElementList = new ArrayList<String[]>();
        List<String> noStoryFromEAList = new ArrayList<String>();
        List<String> noGUIDFromJiraList = new ArrayList<String>();
        List<String> reopenStoryList = new ArrayList<String>();

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

            // Update story key and status
            boolean changed = false;
            if (checkStoryKeyAndUpdateIntoElement(element, guidKeyMap, noStoryFromEAList, noGUIDFromJiraList, noGUIDFromJiraMap)) {
                changed = true;
            }

            if (checkStoryStatusAndUpdateIntoElement(element, keyStoryMap, reopenStoryList)) {
                changed = true;
            }

            // If changed, then return
            if (changed) {
                newElementList.add(element);
            }
        }

        // Info
        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        int infoIndex = 0;
        for (String str : noGUIDFromJiraList) {
            sb.append(String.format("%d, %s", infoIndex++, str));
        }

        sb.append("\n");
        infoIndex = 0;
        for (String str : noStoryFromEAList) {
            sb.append(String.format("%d, %s", infoIndex++, str));
        }
        System.out.println(sb.toString());

        if (newElementList.size() > 0) {
            newElementList.add(0, headers);

            // Add the parent elements
            addParentElements(newElementList, elements);
        }
        return newElementList;
    }

    private static boolean checkStoryStatusAndUpdateIntoElement( String[] element, Map<String, String[]> guidKeyMap, List<String> reopenStoryList) {
        if (ArrayUtils.isEmpty(element) || guidKeyMap == null || guidKeyMap.size() <= 0) {
            return false;
        }

        String key = element[EAHeaderEnum.JiraIssueKey.getIndex()];
        if (StrUtils.isEmpty(key)) {
            return false;
        }

        String[] story = guidKeyMap.get(key);
        if (ArrayUtils.isEmpty(story)) {
            return false;
        }

        // Only changed from Implemented to Approved
        int statusIndex = EAHeaderEnum.Status.getIndex();
        if (EAStatusEnum.isMappedToStory(element[statusIndex])
                && JiraStatusEnum.isClosed(story[JiraHeaderEnum.Status.getIndex()])) {
            EAStatusEnum status = JiraResultEnum.toEAStatus(story[JiraHeaderEnum.Result.getIndex()]);
            if (status != null) {
                element[statusIndex] = status.getCode();
            }
            return true;
        }
        return false;
    }

    /**
     * Check if the story key changes. If so, update to element.
     *
     * @param element
     * @param guidKeyMap
     * @param noStoryFromEAList
     * @param noGUIDFromJiraList
     * @param noGUIDFromJiraMap
     * @return
     */
    private static boolean checkStoryKeyAndUpdateIntoElement(
            String[] element, Map<String, String> guidKeyMap,
            List<String> noStoryFromEAList, List<String> noGUIDFromJiraList, Map<String, String> noGUIDFromJiraMap
    ) {
        if (ArrayUtils.isEmpty(element) || guidKeyMap == null || guidKeyMap.size() <= 0) {
            return false;
        }

        String guid = element[EAHeaderEnum.GUID.getIndex()];
        if (StrUtils.isEmpty(guid)) {
            return false;
        }

        String story = element[EAHeaderEnum.JiraIssueKey.getIndex()];
        String newStory = guidKeyMap.get(guid);
        boolean hasStory = JiraKeyUtil.isValid(story);
        boolean hasNewStory = JiraKeyUtil.isValid(newStory);

        boolean changed = false;
        String name = element[EAHeaderEnum.Name.getIndex()];
        if (!hasStory && !hasNewStory) {
            // Not connected
            if (noStoryFromEAList != null) {
                noStoryFromEAList.add(String.format("GUID doesn't connect with story, while still no one from Jira: %s, %s\n", guid, name));
            }
        } else if (!hasStory && hasNewStory) {
            // New story
            element[EAHeaderEnum.JiraIssueKey.getIndex()] = newStory;
            changed = true;
        } else if (hasStory && !hasNewStory) {
            // Old story but no new story
            if (noGUIDFromJiraList != null) {
                noGUIDFromJiraList.add(String.format("GUID connects with one story, but no GUID from Jira story: %s, %s, %s\n", guid, story, name));
            }

            if (noGUIDFromJiraMap != null) {
                if (noGUIDFromJiraMap.containsValue(story)) {
                    System.out.printf("GUIDs connect with same story: %s, %s, %s\n", guid, story, name);
                }
                noGUIDFromJiraMap.put(guid, story);
            }
        } else if (hasStory && hasNewStory) {
            if (!story.equalsIgnoreCase(newStory)) {
                System.out.printf("GUID connects with multiple stories: %s, %s, %s, %s\n", guid, story, newStory, name);
            }
        }
        return changed;
    }

    /**
     * Add the parent elements into the list.
     *
     * @param childrenElements
     * @param parentElements
     */
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

    /**
     * Generate the SQL
     *
     * @param updateGUIDKeyMap
     * @param keyStoryMap
     * @return
     */
    public static String[] generateUpdateJiraGUIDSQL(Map<String, String> updateGUIDKeyMap, Map<String, String[]> keyStoryMap) {
        if (updateGUIDKeyMap == null || updateGUIDKeyMap.size() <= 0 || keyStoryMap == null || keyStoryMap.size() <= 0) {
            return null;
        }

        List<String> sqlList = new ArrayList<String>(updateGUIDKeyMap.size());
        sqlList.add("set @valueId = (select IFNULL(max(id), 0) from jiradb.customfieldvalue where id < 10424);");

        String customFieldId = "select id from jiradb.customfield where cfname = 'EA-GUID'";
        List<String> issueIds = new ArrayList<String>();
        for (Map.Entry<String, String> guidKey : updateGUIDKeyMap.entrySet()) {
            String issueId = keyStoryMap.get(guidKey.getValue())[JiraHeaderEnum.ID.getIndex()];
            if (StrUtils.isEmpty(issueId) || StrUtils.isEmpty(guidKey.getKey())) {
                System.out.printf("Can't find story id for key: %s, %s\n", guidKey.getKey(), guidKey.getValue());
                continue;
            }

            sqlList.add(String.format(
                    "insert into jiradb.customfieldvalue(id, issue, CUSTOMFIELD, stringvalue) values(%s, %s, (%s), '%s');",
                    "@valueId := @valueId + 1",
                    issueId,
                    customFieldId,
                    guidKey.getKey()
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
