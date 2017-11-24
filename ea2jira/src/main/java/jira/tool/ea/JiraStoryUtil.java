package jira.tool.ea;

import dbtools.common.file.CsvUtil;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.util.*;

public class JiraStoryUtil {
    private static String Jira_Header_GUID = "Custom field (EA-GUID)";
    private static String Jira_Header_Key = "Issue key";
    private static String Jira_Issue_Id = "Issue id";
    private static String Jira_Header_Label = "标签";
    private static String PMO_Label = "PMO-EA导入（禁止私动）";

    public static Map<String, String> getGUIDStoryMap(String csvFile, Map<String, String> storyKeyIdMap, Set<String> pmoLabelKeySet) {
        List<String[]> storyList = CsvUtil.readFile(csvFile);
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
        int idIndex = headerList.indexOf(Jira_Issue_Id);
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

            String story = values[keyIndex];
            String id = values[idIndex];
            if (StrUtils.isEmpty(story) || StrUtils.isEmpty(id) || !JiraIssueKeyUtil.isValid(story)) {
                continue;
            }

            // Map key to id
            story = story.toUpperCase();
            if (storyKeyIdMap != null) {
                storyKeyIdMap.put(story, id);
            }

            // Map the labels
            if (pmoLabelKeySet != null && lableIndexes != null && lableIndexes.size() > 0) {
                for (int labelIndex : lableIndexes) {
                    String label = values[labelIndex];
                    if (PMO_Label.equalsIgnoreCase(label)) {
                        pmoLabelKeySet.add(story);
                        break;
                    }
                }
            }

            // Map GUID to story key
            if (guidIndex >= 0 && guidIndex < values.length) {
                String guid = values[guidIndex];
                if (!StrUtils.isEmpty(guid)) {
                    if (guidStoryMap.containsKey(guid)) {
                        System.out.printf("GUID connects with multiple stories: %s, %s, %s\n", guid, guidStoryMap.get(guid), story);
                    } else {
                        guidStoryMap.put(guid, story);
                    }
                }
            }
        }

        return guidStoryMap;
    }
}
