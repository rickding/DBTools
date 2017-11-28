package jira.tool.ea;

import dbtools.common.file.CsvUtil;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;

import java.util.*;

public class EA2Jira {
    public static String Jira_Date_Format = "yyyy/MM/dd";
    private static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");

    private static String processEstimation(String value) {
        if (StrUtils.isEmpty(value) || StrUtils.isEmpty(value.trim())) {
            return null;
        }

        value = value.trim().toLowerCase();
        try {
            int base = 0;
            double v = 0.0;
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
                base = 8 * 3600; // default as a day
            }

            int tmp = (int) (v * base);
            return tmp <= 0 ? null : String.format("%d", tmp);
        } catch (Exception e) {
            System.out.printf("Error when process estimation: %s\n", value);
        }
        return null;
    }

    private static String processDueDate(String value) {
        if (StrUtils.isEmpty(value) || StrUtils.isEmpty(value.trim())) {
            return null;
        }

        value = value.trim();
        for (String format : new String[]{
                "yyyyMMdd", "yyyy.MM.dd", "yyyy-MM-dd", "yyyy/MM/dd", "yyyy/MMdd", "yyyy年MM月dd日", "yyyy年MM月dd号",
                "yyMMdd", "yy.MM.dd", "yy-MM-dd", "yy/MM/dd", "yy/MMdd", "yy年MM月dd日", "yy年MM月dd号",
                "MMdd", "MM.dd", "MM-dd", "MM/dd", "MM月dd日", "MM月dd号",
        }) {
            Date date = DateUtils.parse(value, format, false);
            if (date != null) {
                // Adjust the year if it's not set
                if (format.length() < 6) {
                    String strDate = DateUtils.format(date, "MM-dd");
                    int year = Integer.valueOf(DateUtils.format(today, "yyyy"));
                    int month = Integer.valueOf(DateUtils.format(today, "MM"));
                    if (month >= 12 && strDate.compareTo(DateUtils.format(today, "MM-dd")) < 0) {
                        year++;
                    }
                    date = DateUtils.parse(String.format("%4d-%s", year, strDate), "yyyy-MM-dd");
                }

                // Format date
                return DateUtils.format(date, Jira_Date_Format);
            }
        }
        return null;
    }

    private static String processValue(JiraHeaderEnum jiraHeaderEnum, String value) {
        if (jiraHeaderEnum == null || StrUtils.isEmpty(jiraHeaderEnum.getCode())) {
            return value;
        }

        String jiraHeader = jiraHeaderEnum.getCode();

        // Find the user name
        for (JiraHeaderEnum tmp : new JiraHeaderEnum[]{JiraHeaderEnum.Developer, JiraHeaderEnum.Owner, JiraHeaderEnum.PM}) {
            if (jiraHeader.equalsIgnoreCase(tmp.getCode())) {
                JiraUser user = JiraUser.findUser(value);
                if (user == null) {
                    System.out.printf("Error when find user: %s\n", value);
                } else {
                    return user.getName();
                }
            }
        }

        // Estimation
        if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Estimation.getCode())) {
            return processEstimation(value);
        }

        // DueDate
        for (JiraHeaderEnum tmp : new JiraHeaderEnum[]{JiraHeaderEnum.DueDate, JiraHeaderEnum.QAStartDate, JiraHeaderEnum.QAFinishDate}) {
            if (jiraHeader.equalsIgnoreCase(tmp.getCode())) {
                value = processDueDate(value);

                // QA start 2 days earlier
                if (!StrUtils.isEmpty(value) && jiraHeader.equalsIgnoreCase(JiraHeaderEnum.QAStartDate.getCode())) {
                    Date date = DateUtils.parse(value, Jira_Date_Format);
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
                        value = DateUtils.format(date, Jira_Date_Format);
                    }
                }

                return value;
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
     *
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
        Map<String, String[]> keyElementMap = EAElementUtil.getKeyElementMap(elementList);
        String projectName = ArrayUtils.isEmpty(project.getPrefixes()) ? null : project.getPrefixes()[0];

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

            // Check if the estimation and due-date are valid
            if (StrUtils.isEmpty(processEstimation(element[EAHeaderEnum.Estimation.getIndex()]))
                    || StrUtils.isEmpty(processDueDate(element[EAHeaderEnum.DueDate.getIndex()]))) {
                continue;
            }

            // Check if it has jira issue key already
            String issueKey = element[EAHeaderEnum.JiraIssueKey.getIndex()];
            if (JiraIssueKeyUtil.isValid(issueKey)) {
                issueKey = issueKey.trim().toUpperCase();
                // Return the pre-created story if it has no pmo label
                if (preCreatedStoryList != null && !preCreatedStoryList.contains(issueKey) && (pmoLabelKeySet == null || !pmoLabelKeySet.contains(issueKey))) {
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
                    JiraUser user = JiraUser.findUser(value);
                    if (user == null) {
                        System.out.printf("Error when find user: %s\n", value);
                    } else {
                        team = user.getTeam();
                    }
                } else if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.EAGUID.getCode())) {
                    guid = value;
                } else if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Description.getCode())) {
                    String parentPath = EAElementUtil.getParentPath(element, keyElementMap);
                    value = formatDescription(projectName, parentPath, value);
                } else if (jiraHeader.equalsIgnoreCase(JiraHeaderEnum.Title.getCode())) {
                    if (!StrUtils.isEmpty(value) && !StrUtils.isEmpty(value.trim())) {
                        int level = value.length();
                        if (level < 10) {
                            if (level > 5) {
                                level = 1;
                            } else if (level > 3) {
                                level = 2;
                            } else {
                                level = 3;
                            }
                            String parentPath = EAElementUtil.getParentPath(element, keyElementMap, level, 15);
                            if (!StrUtils.isEmpty(parentPath)) {
                                value = StrUtils.isEmpty(value) ? parentPath : String.format("%s: %s", parentPath, value);
                            }
                        }
                    }
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

    private static String formatDescription(String projectName, String parentPath, String desc) {
        if (StrUtils.isEmpty(projectName) && StrUtils.isEmpty(parentPath)) {
            return desc;
        }

        StringBuilder sb = new StringBuilder();

        // EA file
        if(!StrUtils.isEmpty(projectName)) {
            sb.append(String.format("EA_file: %s.EAP \r\n\r\n", projectName));
        }

        // EA parent package
        if (!StrUtils.isEmpty(parentPath)) {
            sb.append(String.format("EA_package: %s \r\n\r\n", parentPath));
        }

        // Original note
        if (!StrUtils.isEmpty(desc)) {
            sb.append(desc);
        }

        return sb.toString();
    }
}
