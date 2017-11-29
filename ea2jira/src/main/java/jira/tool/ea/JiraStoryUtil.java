package jira.tool.ea;

import dbtools.common.file.CsvUtil;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.io.File;
import java.util.*;

public class JiraStoryUtil {
    private static String Jira_File = "EA-PMO-all (上海欧电云信息科技有限公司).csv";
    private static String PMO_Label = "PMO-EA导入（禁止私动）";

    private static String Jira_Header_GUID = "Custom field (EA-GUID)";
    private static String Jira_Header_Key = "Issue key";
    private static String Jira_Header_Id = "Issue id";
    private static String Jira_Header_Result = "解决结果";
    private static String Jira_Header_Status = "状态";
    private static String Jira_Header_Label = "标签";

    public static Map<String, String> getGUIDKeyMap(Set<String> filePaths, Map<String, String> keyStoryMap, Set<String> pmoLabelKeySet) {
        if (filePaths == null || filePaths.size() <= 0) {
            return null;
        }

        for (String filePath : filePaths) {
            File file = new File(filePath);
            if (file.isDirectory()) {
                List<String[]> storyList = CsvUtil.readFile(String.format("%s\\%s", filePath, Jira_File));
                Map<String, String> guidKeyMap = getGUIDKeyMap(storyList, keyStoryMap, pmoLabelKeySet);
                if (guidKeyMap != null && guidKeyMap.size() > 0) {
                    return guidKeyMap;
                }
            }
        }
        return null;
    }

    public static Map<String, String> getGUIDKeyMap(List<String[]> storyList, Map<String, String> keyStoryMap, Set<String> pmoLabelKeySet) {
        if (storyList == null || storyList.size() <= 1) {
            return null;
        }

        // Check headers firstly
        int storyIndex = 0;
        String[] headers = storyList.get(storyIndex++);
        if (ArrayUtils.isEmpty(headers)) {
            return null;
        }

        List<String> headerList = Arrays.asList(headers);
        int keyIndex = headerList.indexOf(Jira_Header_Key);
        int idIndex = headerList.indexOf(Jira_Header_Id);
        int guidIndex = headerList.indexOf(Jira_Header_GUID);

        if (keyIndex < 0 || idIndex < 0) {
            System.out.printf("Can't find the headers: %s\n", headerList.toString());
            return null;
        }

        // Find the label index list
        List<Integer> lableIndexes = new ArrayList<Integer>();
        for (int headerIndex = 0; headerIndex < headerList.size(); headerIndex++) {
            if (Jira_Header_Label.equalsIgnoreCase(headerList.get(headerIndex))) {
                lableIndexes.add(headerIndex);
            }
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
                keyStoryMap.put(key, id);
            }

            // Map the labels
            if (pmoLabelKeySet != null && lableIndexes != null && lableIndexes.size() > 0) {
                for (int labelIndex : lableIndexes) {
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
                if (!StrUtils.isEmpty(guid)) {
                    if (guidStoryMap.containsKey(guid)) {
                        System.out.printf("GUID connects with multiple stories: %s, %s, %s\n", guid, guidStoryMap.get(guid), key);
                    } else {
                        guidStoryMap.put(guid, key);
                    }
                }
            }
        }

        return guidStoryMap;
    }
}
