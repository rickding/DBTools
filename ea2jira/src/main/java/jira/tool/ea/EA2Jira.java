package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import ea.tool.api.EAQAUtil;
import jira.tool.db.model.User;

import java.util.*;

public class EA2Jira {
    public static String Jira_Date_Format = "yyyy/MM/dd";
    private static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");
    private static String svnUrl = "http://svn.odianyun.local/svn/doc/30-项目/PMO/需求内容确认文件夹";
    private static String svnUrl2 = "http://svn.odianyun.local/svn/doc/30-项目/PMO/需求内容提交文件夹";

    private static String processValue(EA2JiraHeaderEnum jiraHeaderEnum, String value) {
        if (jiraHeaderEnum == null || StrUtils.isEmpty(jiraHeaderEnum.getCode())) {
            return value;
        }

        String jiraHeader = jiraHeaderEnum.getCode();

        // Find the user name
        for (EA2JiraHeaderEnum tmp : new EA2JiraHeaderEnum[]{EA2JiraHeaderEnum.Developer, EA2JiraHeaderEnum.Owner, EA2JiraHeaderEnum.PM}) {
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
        if (jiraHeader.equalsIgnoreCase(EA2JiraHeaderEnum.Estimation.getCode())) {
            return EAEstimationUtil.processEstimation(value);
        }

        // DueDate
        for (EA2JiraHeaderEnum tmp : new EA2JiraHeaderEnum[]{EA2JiraHeaderEnum.DueDate, EA2JiraHeaderEnum.QAStartDate, EA2JiraHeaderEnum.QAFinishDate}) {
            if (jiraHeader.equalsIgnoreCase(tmp.getCode())) {
                value = EADateUtil.processDueDate(value, today);

                // QA start 2 days earlier
                if (!StrUtils.isEmpty(value) && jiraHeader.equalsIgnoreCase(EA2JiraHeaderEnum.QAStartDate.getCode())) {
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
        if (EA2JiraHeaderEnum.JiraHeaderValueMap.containsKey(jiraHeaderEnum)) {
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
        Map<EA2JiraHeaderEnum, EAHeaderEnum> headerMap = EA2JiraHeaderEnum.JiraEAHeaderMap;
        EA2JiraHeaderEnum[] jiraHeaders = EA2JiraHeaderEnum.getSavedHeaders();
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
            if (StrUtils.isEmpty(EAEstimationUtil.processEstimation(element[EAHeaderEnum.Estimation.getIndex()]))
                    || StrUtils.isEmpty(EADateUtil.processDueDate(element[EAHeaderEnum.DueDate.getIndex()], today))) {
                continue;
            }

            // Check if the author exists
            if (StrUtils.isEmpty(element[EAHeaderEnum.Author.getIndex()]) || StrUtils.isEmpty(element[EAHeaderEnum.Owner.getIndex()])) {
                continue;
            }

            // Check if it has jira issue key already
            String issueKey = element[EAHeaderEnum.JiraIssueKey.getIndex()];
            if (JiraKeyUtil.isValid(issueKey)) {
                // Return the pre-created story if it has no pmo label
                issueKey = issueKey.trim().toUpperCase();
                if ((pmoLabelKeySet == null || !pmoLabelKeySet.contains(issueKey)) && preCreatedStoryList != null) {
                    if (!preCreatedStoryList.contains(issueKey)) {
                        preCreatedStoryList.add(issueKey);
                    } else {
                        System.out.printf("Duplicated issue in pre-created story info: %s\r\n", issueKey, element.toString());
                    }
                }
                continue;
            }

            // Fill jira dataMap
            String team = "Can't find Jira User";
            String[] values = new String[jiraHeaders.length];
            String guid = null;
            int headerIndex = 0;

            for (EA2JiraHeaderEnum jiraHeaderEnum : jiraHeaders) {
                if (jiraHeaderEnum == null || StrUtils.isEmpty(jiraHeaderEnum.getCode())) {
                    headerIndex++;
                    continue;
                }

                EAHeaderEnum eaHeader = headerMap.get(jiraHeaderEnum);
                String value = eaHeader == null ? null : element[eaHeader.getIndex()];
                value = processValue(jiraHeaderEnum, value);

                // Special values
                String jiraHeader = jiraHeaderEnum.getCode();
                if (jiraHeader.equalsIgnoreCase(EA2JiraHeaderEnum.Project.getCode())) {
                    // The project maps with file name
                    value = project.getName();
                } else if (jiraHeader.equalsIgnoreCase(EA2JiraHeaderEnum.Developer.getCode())) {
                    // Team of the developer
                    JiraUser user = JiraUser.findUser(value);
                    if (user == null) {
                        System.out.printf("Error when find user: %s\n", value);
                    } else {
                        team = user.getTeam();
                    }
                } else if (jiraHeader.equalsIgnoreCase(EA2JiraHeaderEnum.EAGUID.getCode())) {
                    guid = value;
                } else if (jiraHeader.equalsIgnoreCase(EA2JiraHeaderEnum.Description.getCode())) {
                    // Format desc with ea file and package path
                    String parentPath = EAElementUtil.getParentPath(element, keyElementMap);
                    value = formatDescription(projectName, parentPath, value, element[EAHeaderEnum.Keywords.getIndex()]);
                } else if (jiraHeader.equalsIgnoreCase(EA2JiraHeaderEnum.Title.getCode())) {
                    // Format title with package
                    value = formatTitle(value, element, keyElementMap);
                }

                values[headerIndex++] = value;
            }

            if (StrUtils.isEmpty(guid)) {
                System.out.printf("Error with empty GUID: %s\n", Arrays.asList(values));
                continue;
            }

            // Check if GUID exists in story
            if (guidStoryMap != null && guidStoryMap.containsKey(guid)) {
                String tmp = guidStoryMap.get(guid);
                System.out.printf("GUID already connects with one story: %s, %s, %s\r\n", guid, tmp, keyElementMap.get(tmp));
                continue;
            }

            // Group as team
            List<String[]> stories = teamStoryListMap.get(team);
            if (stories == null) {
                stories = new ArrayList<String[]>();
                teamStoryListMap.put(team, stories);
            }

            // Update and add story
            updateQA(values, team, element);
            JiraEpicUtil.updateEpic(values);
            stories.add(values);
        }
    }

    private static void updateQA(String[] values, String team, String[] element) {
        if (ArrayUtils.isEmpty(values) || EA2JiraHeaderEnum.QA.getIndex() >= values.length) {
            return;
        }

        int qaIndex = EA2JiraHeaderEnum.QA.getIndex();

        // Get QA from notes
        if (element != null && element.length > 0) {
            String notes = element[EAHeaderEnum.Notes.getIndex()];
            String[] qaArray = EAQAUtil.getQAArray(notes);
            if (qaArray != null && qaArray.length > 0) {
                JiraUser qa = JiraUser.findUser(qaArray[0]);
                if (qa != null) {
                    values[qaIndex] = qa.getName();
                    return;
                }
            }
        }

        // Get QA from team
        if (!StrUtils.isEmpty(team)) {
            JiraQAEnum qa = JiraQAEnum.findQA(team);
            if (qa != null) {
                values[qaIndex] = qa.getCode();
            }
        }
    }

    private static String formatTitle(String title, String[] element, Map<String, String[]> keyElementMap) {
        if (StrUtils.isEmpty(title) || StrUtils.isEmpty(title.trim()) || ArrayUtils.isEmpty(element) || keyElementMap == null || keyElementMap.size() <= 0) {
            return title;
        }

        int level = title.trim().length();
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
                title = StrUtils.isEmpty(title) ? parentPath : String.format("%s: %s", parentPath, title);
            }
        }
        return title;
    }

    private static String formatDescription(String projectName, String parentPath, String desc, String keywords) {
        if (StrUtils.isEmpty(projectName) && StrUtils.isEmpty(parentPath)) {
            return desc;
        }

        StringBuilder sb = new StringBuilder();

        // EA file
        if(!StrUtils.isEmpty(projectName)) {
            sb.append(String.format("【EA文件】%s/%s.EAP (PMO整理前、产品提交的svn目录为: %s), \r\n\r\n", svnUrl, projectName, svnUrl2));
        }

        // EA parent package
        if (!StrUtils.isEmpty(parentPath)) {
            sb.append(String.format("【EA包路径】%s, \r\n\r\n", parentPath));
        }

        // Keywords
        if (!StrUtils.isEmpty(keywords)) {
            sb.append(String.format("【关键词】%s \r\n\r\n", keywords));
        }

        // Original note
        if (!StrUtils.isEmpty(desc)) {
            sb.append(String.format("【注释】%s \r\n\r\n", desc));
        }
        return sb.toString();
    }
}
