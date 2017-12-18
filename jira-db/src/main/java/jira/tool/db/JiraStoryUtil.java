package jira.tool.db;

import dbtools.common.file.CsvUtil;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.db.model.Story;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JiraStoryUtil {
    private static String Jira_File = "EA-PMO-all (上海欧电云信息科技有限公司).csv";
    private static String PMO_Label = "PMO-EA导入（禁止私动）";

    private static Map<String, Story> mapKeyStory = null;

    public static Story findStory(String key) {
        if (!JiraKeyUtil.isValid(key)) {
            return null;
        }

        synchronized ("findStory.getStoryList") {
            if (mapKeyStory == null) {
                List<Story> storyList = DBUtil.getStoryList();
                if (storyList != null && storyList.size() > 0) {
                    mapKeyStory = new HashMap<String, Story>(storyList.size());
                    for (Story story : storyList) {
                        mapKeyStory.put(story.getKey().trim().toUpperCase(), story);
                    }
                }
            }
        }

        if (mapKeyStory == null || mapKeyStory.size() <= 0) {
            return null;
        }
        return mapKeyStory.get(key.trim().toUpperCase());
    }

    public static Map<String, String> getGUIDKeyMap(Set<String> filePaths, Map<String, String[]> keyStoryMap, Set<String> pmoLabelKeySet) {
        if (filePaths == null || filePaths.size() <= 0) {
            return null;
        }

        // Read story from db and convert
        List<String[]> storyList = JiraHeaderEnum.formatStoryList(DBUtil.getPMOStoryList());

        if (storyList == null || storyList.size() <= 0) {
            // Read from csv file
            for (String filePath : filePaths) {
                File file = new File(filePath);
                if (file.isDirectory()) {
                    storyList = CsvUtil.readFile(String.format("%s\\%s", filePath, Jira_File));
                    if (storyList != null && storyList.size() > 0) {
                        break;
                    }
                }
            }
        }

        // Process
        Map<String, String> guidKeyMap = getGUIDKeyMap(storyList, keyStoryMap, pmoLabelKeySet);
        if (guidKeyMap != null && guidKeyMap.size() > 0) {
            return guidKeyMap;
        }
        return null;
    }

    private static Map<String, String> getGUIDKeyMap(List<String[]> storyList, Map<String, String[]> keyStoryMap, Set<String> pmoLabelKeySet) {
        if (storyList == null || storyList.size() <= 1) {
            return null;
        }

        // Check headers firstly
        int storyIndex = 0;
        String[] headers = storyList.get(storyIndex++);
        if (ArrayUtils.isEmpty(headers)) {
            return null;
        }

        List<Integer> labelIndexes = JiraHeaderEnum.fillIndex(headers);
        int keyIndex = JiraHeaderEnum.Key.getIndex();
        int idIndex = JiraHeaderEnum.ID.getIndex();
        int guidIndex =JiraHeaderEnum.EAGUID.getIndex();

        if (keyIndex < 0 || idIndex < 0) {
            System.out.printf("Invalid headers: %s\n", Arrays.asList(headers).toString());
            return null;
        }

        // Walk through the data
        Map<String, String> guidStoryMap = new HashMap<String, String>();
        for (; storyIndex < storyList.size(); storyIndex++) {
            String[] values = storyList.get(storyIndex);
            if (ArrayUtils.isEmpty(values) || keyIndex >= values.length || idIndex >= values.length) {
                continue;
            }

            String key = values[keyIndex];
            String id = values[idIndex];
            if (StrUtils.isEmpty(key) || StrUtils.isEmpty(id) || !JiraKeyUtil.isValid(key)) {
                continue;
            }

            // Map key to story values
            key = key.toUpperCase();
            if (keyStoryMap != null) {
                keyStoryMap.put(key, values);
            }

            // Map the labels
            if (pmoLabelKeySet != null && labelIndexes != null && labelIndexes.size() > 0) {
                for (int labelIndex : labelIndexes) {
                    String label = values[labelIndex];
                    if (PMO_Label.equalsIgnoreCase(label)) {
                        pmoLabelKeySet.add(key);
                        break;
                    }
                }
            }

            // Map GUID to story key
            if (guidIndex >= 0 && guidIndex < values.length) {
                String guid = values[guidIndex];
                if (!StrUtils.isEmpty(guid) && !JiraTeamEnum.isStoryInIgnoredTeam(key)) {
                    if (guidStoryMap.containsKey(guid)) {
                        System.out.printf("Jira: GUID connects with multiple stories: %s, %s, %s\r\n", guid, guidStoryMap.get(guid), key);
                    } else {
                        guidStoryMap.put(guid, key);
                    }
                }
            }
        }

        return guidStoryMap;
    }
}
