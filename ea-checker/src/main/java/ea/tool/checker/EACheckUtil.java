package ea.tool.checker;

import dbtools.common.file.ExcelUtil;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import ea.tool.api.EAHeaderEnum;
import ea.tool.api.EAQAUtil;
import ea.tool.api.EATypeEnum;
import jira.tool.ea.EADateUtil;
import ea.tool.api.EAEstimationUtil;
import ea.tool.api.EAStatusEnum;
import jira.tool.db.JiraKeyUtil;
import jira.tool.ea.JiraProjectEnum;
import jira.tool.db.JiraUser;
import jira.tool.ea.PMOMeetingUtil;
import jira.tool.ea.JiraUserImpl;
import org.apache.poi.xssf.usermodel.XSSFSheet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EACheckUtil {
    private static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");

    public static void process(
            JiraProjectEnum project, List<String[]> elementList,
            Map<String, List<String[]>> teamElementListMap, List<String> preCreatedStoryList,
            Map<String, List<String[]>> unReadyElementListMap
    ) {
        if (project == null || elementList == null || elementList.size() <= 0) {
            return;
        }

        // Headers
        int rowStart = 0;
        int rowEnd = elementList.size() - 1;
        EAHeaderEnum.fillIndex(elementList.get(rowStart++));

        // Data
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

            // Check the created and modified dates: since the last meeting: Tuesday or Friday
            if (!EADateUtil.needsToBeProcessed(PMOMeetingUtil.getLastMeetingDate(), element[EAHeaderEnum.CreatedDate.getIndex()])
                    && !EADateUtil.needsToBeProcessed(PMOMeetingUtil.getLastMeetingDate(), element[EAHeaderEnum.ModifiedDate.getIndex()])) {
                continue;
            }

            // Check if the author and owner exist
            if (JiraUser.findUser(element[EAHeaderEnum.Author.getIndex()]) == null
                    || JiraUser.findUser(element[EAHeaderEnum.Dev.getIndex()]) == null) {
                continue;
            }

            // Check QA
            if (JiraUser.findUser(element[EAHeaderEnum.QA.getIndex()]) == null) {
                // Find in notes again with query db
                String user = EAQAUtil.findQAInNotes(element[EAHeaderEnum.Notes.getIndex()], new JiraUserImpl());
                if (user != null && user.trim().length() > 0) {
                    element[EAHeaderEnum.QA.getIndex()] = user;
                } else {
//                    continue;
                }
            }

            // Check if the estimation and due-date are valid
            if (StrUtils.isEmpty(EAEstimationUtil.processEstimation(element[EAHeaderEnum.Estimation.getIndex()]))
                    || StrUtils.isEmpty(EADateUtil.processDueDate(element[EAHeaderEnum.DueDate.getIndex()], today))) {
                continue;
            }

            // Valid
            if (teamElementListMap != null) {
                // Group as team
                String team = JiraUser.findUser(element[EAHeaderEnum.Dev.getIndex()]).getTeam();
                List<String[]> stories = teamElementListMap.get(team);
                if (stories == null) {
                    stories = new ArrayList<String[]>();
                    teamElementListMap.put(team, stories);
                }

                // Add
                stories.add(element);
            }

            // Check if it has jira issue key already
            String issueKey = element[EAHeaderEnum.JiraIssueKey.getIndex()];
            if (JiraKeyUtil.isValid(issueKey)) {
                // Return the pre-created story if it has no pmo label
                if (preCreatedStoryList != null) {
                    issueKey = issueKey.trim().toUpperCase();
                    if (!preCreatedStoryList.contains(issueKey)) {
                        preCreatedStoryList.add(issueKey);
                    } else {
                        System.out.printf("Duplicated issue in pre-created story info: %s\r\n", issueKey, element.toString());
                    }
                }
            }
        }
    }

    public static int fillExcel(XSSFSheet sheet, Map<String, List<String[]>> teamStoryListMap, boolean hasHeaders) {
        if (sheet == null || teamStoryListMap == null || teamStoryListMap.size() <= 0) {
            return 0;
        }

        // Get the headers
        List<String[]> stories = new ArrayList<String[]>() {{
            add(EAHeaderEnum.getHeaders());
        }};

        // Write data to excel
        for (Map.Entry<String, List<String[]>> teamStories : teamStoryListMap.entrySet()) {
            if (hasHeaders) {
                List<String[]> tmpList = teamStories.getValue();
                tmpList = tmpList.subList(1, tmpList.size());
                stories.addAll(tmpList);
            } else {
                stories.addAll(teamStories.getValue());
            }
        }

        ExcelUtil.fillSheet(sheet, stories);
        return stories.size() - 1;
    }
}
