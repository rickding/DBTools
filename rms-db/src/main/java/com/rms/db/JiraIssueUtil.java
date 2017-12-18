package com.rms.db;

import com.rms.db.model.JiraIssue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JiraIssueUtil {
    private static Map<String, JiraIssue> nameItemMap = null;

    public static JiraIssue findOrAdd(final String key, final String name, final String guid) {
        if (key == null || key.trim().length() <= 0) {
            return null;
        }

        if (nameItemMap == null) {
            nameItemMap = list2Map(DBUtil.getJiraIssueList(), null);
        }

        // Find
        if (nameItemMap != null) {
            String nameStr = key.trim().toUpperCase();
            if (nameItemMap.containsKey(nameStr)) {
                return nameItemMap.get(nameStr);
            }

//            for (JiraIssue item : nameItemMap.values()) {
//                if (guid != null && item.getGuid() != null && guid.trim().equalsIgnoreCase(item.getGuid().trim())) {
//                    return item;
//                }
//                if (name != null && item.getName() != null && name.trim().equalsIgnoreCase(item.getName().trim())) {
//                    return item;
//                }
//            }
        }

        // Add new one
        JiraIssue item = DBUtil.addJiraIssue(new JiraIssue() {{
            setIssueKey(key.trim());
            setName(name == null || name.trim().length() <= 0 ? key.trim() : name.trim());

            if (guid != null && guid.trim().length() > 0) {
                setGuid(guid);
            }
        }});

        if (item == null) {
            System.out.printf("Fail to add new jira issue: %s\r\n", key);
        } else {
            System.out.printf("Add new jira issue: %s\r\n", key);
            nameItemMap = null;
        }
        return item;
    }

    private static Map<String, JiraIssue> list2Map(List<JiraIssue> list, Map<String, String> guidKeyMap) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<String, JiraIssue> map = new HashMap<String, JiraIssue>(list.size());
        for (JiraIssue item : list) {
            map.put(item.getIssueKey().trim().toUpperCase(), item);

            if (guidKeyMap != null && item.getGuid() != null) {
                guidKeyMap.put(item.getGuid().trim().toUpperCase(), item.getIssueKey().trim().toUpperCase());
            }
        }
        return map;
    }
}
