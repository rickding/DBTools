package jira.tool.ea;

import dbtools.common.file.CsvUtil;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;

import java.util.*;

public class EA2Jira {
    public static String Jira_Date_Format = "yyyy/MM/dd";
    private static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");

    private static String Jira_Header_GUID = "Custom field (EA-GUID)";
    private static String Jira_Header_Key = "Issue key";
    private static String Jira_Issue_Id = "Issue id";
    private static String Jira_Header_Label = "标签";
    private static String PMO_Label = "PMO-EA导入（禁止私动）";

    private static String processValue(JiraHeaderEnum jiraHeaderEnum, String value) {
        if (jiraHeaderEnum == null || StrUtils.isEmpty(jiraHeaderEnum.getCode())) {
            return value;
        }

        String jiraHeader = jiraHeaderEnum.getCode();

        // Find the user name
        for (JiraHeaderEnum tmp : new JiraHeaderEnum[]{JiraHeaderEnum.Developer, JiraHeaderEnum.Owner, JiraHeaderEnum.PM}) {
            if (jiraHeader.equalsIgnoreCase(tmp.getCode())) {
                JiraUserEnum user = JiraUserEnum.findUser(value);
                if (user == null) {
                    System.out.printf("Error when find user: %s\n", value);
                } else {
                    return user.getName();
                }
            }
        }

        // Estimation
        if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Estimation.getCode())) {
            if (!StrUtils.isEmpty(value)) {
                int base = 8 * 3600; // default as a day
                double v = 1.0; // default as one
                value = value.toLowerCase();
                try {
                    if (value.endsWith("h")) {
                        v = Double.valueOf(value.substring(0, value.length() - 1));
                        base = 3600;
                    } else if (value.endsWith("d")) {
                        v = Double.valueOf(value.substring(0, value.length() - 1));
                        base = 8 * 3600;
                    } else if (value.endsWith("w")) {
                        v = Double.valueOf(value.substring(0, value.length() - 1));
                        base = 5 * 8 * 3600;
                    } else {
                        v = Double.valueOf(value);
                    }
                } catch (Exception e) {
                    System.out.printf("Error when process value: %s, %s\n", jiraHeader, value);
                }
                return String.format("%d", (int) (v * base));
            }
        }

        // DueDate
        for (JiraHeaderEnum tmp : new JiraHeaderEnum[]{JiraHeaderEnum.DueDate, JiraHeaderEnum.QAStartDate, JiraHeaderEnum.QAFinishDate}) {
            if (jiraHeader.equalsIgnoreCase(tmp.getCode())) {
                String[] formats = new String[]{
                        "yyyyMMdd", "yyyy.MM.dd", "yyyy-MM-dd", "yyyy/MM/dd", "yyyy/MMdd",
                        "yyMMdd", "yy.MM.dd", "yy-MM-dd", "yy/MM/dd",
                        "MMdd", "MM.dd", "MM-dd", "MM/dd"
                };
                for (String format : formats) {
                    try {
                        Date date = DateUtils.parse(value, format, false);
                        if (date != null) {
                            // Adjust the year if it's not set
                            if (format.length() < 8) {
                                String strDate = DateUtils.format(date, "MM-dd");
                                int year = Integer.valueOf(DateUtils.format(today, "yyyy"));
                                int month = Integer.valueOf(DateUtils.format(today, "MM"));
                                if (month >= 12 && strDate.compareTo(DateUtils.format(today, "MM-dd")) < 0) {
                                    year++;
                                }
                                date = DateUtils.parse(String.format("%4d-%s", year, strDate), "yyyy-MM-dd");
                            }

                            // QA start 2 days earlier
                            if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.QAStartDate.getCode())) {
                                int days = DateUtils.diffDays(date, today);
                                if (days > 3) {
                                    days = 2;
                                } else if (days > 1) {
                                    days = 1;
                                } else {
                                    days = 0;
                                }

                                if (days > 0) {
                                    date = DateUtils.adjustDate(date, -days);
                                }
                            }

                            // Format date
                            value = DateUtils.format(date, Jira_Date_Format);
                            return value;
                        }
                    } catch (Exception e) {
                    }
                }
            }
        }

        // The specified values
        if (JiraHeaderEnum.JiraHeaderValueMap.containsKey(jiraHeaderEnum)) {
            return jiraHeaderEnum.JiraHeaderValueMap.get(jiraHeaderEnum);
        }

        return value;
    }

    /**
     * Return the processed story list
     * @param project
     * @param elementList
     * @param teamStoryListMap
     * @return
     */
    public static void process(
            JiraProjectEnum project, List<String[]> elementList, Map<String, List<String[]>> teamStoryListMap,
            Map<String, String> guidStoryMap, List<String> preCreatedStoryList, Set<String> pmoLabelKeySet
    ) {
        if (project == null || elementList == null || elementList.size() <= 0) {
            return;
        }

        int rowStart = 0;
        int rowEnd = elementList.size() - 1;

        // Headers
        EAHeaderEnum.fillIndex(elementList.get(rowStart++));

        // Prepare firstly
        Map<JiraHeaderEnum, EAHeaderEnum> headerMap = JiraHeaderEnum.JiraEAHeaderMap;
        JiraHeaderEnum[] jiraHeaders = JiraHeaderEnum.getSortedHeaders();

        // Data to stories
        while (rowStart <= rowEnd) {
            String[] element = elementList.get(rowStart++);
            if (ArrayUtils.isEmpty(element)) {
                continue;
            }

            // Only process implemented requirement as story
            if (!EATypeEnum.isMappedToStory(element[EAHeaderEnum.Type.getIndex()])
                    || !EAStatusEnum.isMappedToStory(element[EAHeaderEnum.Status.getIndex()])) {
                continue;
            }

            // Check the created and modified dates
            if (!EADateUtil.needsToBeProcessed(element[EAHeaderEnum.CreatedDate.getIndex()])
                    && !EADateUtil.needsToBeProcessed(element[EAHeaderEnum.ModifiedDate.getIndex()])) {
                continue;
            }

            // Check if it has jira issue key already
            String issueKey = element[EAHeaderEnum.JiraIssueKey.getIndex()];
            if (JiraIssueKeyUtil.isValid(issueKey)) {
                issueKey = issueKey.trim().toUpperCase();
                // Return the pre-created story
                if (preCreatedStoryList != null && (pmoLabelKeySet == null || !pmoLabelKeySet.contains(issueKey))) {
                    preCreatedStoryList.add(issueKey);
                }
                continue;
            }

            // Fill jira dataMap
            String team = "Can't find Jira User";
            String[] values = new String[jiraHeaders.length];
            String guid = null;
            int headerIndex = 0;

            for (JiraHeaderEnum jiraHeaderEnum : jiraHeaders) {
                if (jiraHeaderEnum == null || StrUtils.isEmpty(jiraHeaderEnum.getCode())) {
                    headerIndex++;
                    continue;
                }

                EAHeaderEnum eaHeader = headerMap.get(jiraHeaderEnum);
                String value = eaHeader == null ? null : element[eaHeader.getIndex()];
                value = processValue(jiraHeaderEnum, value);

                // Special values
                String jiraHeader = jiraHeaderEnum.getCode();
                if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Project.getCode())) {
                    // The project maps with file name
                    value = project.getName();
                } else if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Developer.getCode())) {
                    // Team of the developer
                    JiraUserEnum user = JiraUserEnum.findUser(value);
                    if (user == null) {
                        System.out.printf("Error when find user: %s\n", value);
                    } else {
                        team = user.getTeam();
                    }
                } else if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.EAGUID.getCode())) {
                    guid = value;
                }

                values[headerIndex++] = value;
            }

            if (StrUtils.isEmpty(guid)) {
                System.out.printf("Error with empty GUID: %s\n", Arrays.asList(values));
                continue;
            }

            // Check if GUID exists in story
            if (guidStoryMap != null && guidStoryMap.containsKey(guid)) {
                System.out.printf("GUID already connects with one story: %s, %s\n", guid, guidStoryMap.get(guid));
                continue;
            }

            // Group as team
            List<String[]> stories = teamStoryListMap.get(team);
            if (stories == null) {
                stories = new ArrayList<String[]>();
                teamStoryListMap.put(team, stories);
            }
            stories.add(values);
        }
    }

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
            if (StrUtils.isEmpty(story) || StrUtils.isEmpty(id) || !JiraIssueKeyUtil.isValid(story) ) {
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
