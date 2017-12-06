package ea.tool.checker;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;
import jira.tool.ea.EADateUtil;
import jira.tool.ea.EAEstimationUtil;
import jira.tool.ea.EAHeaderEnum;
import jira.tool.ea.EAStatusEnum;
import jira.tool.ea.EATypeEnum;
import jira.tool.ea.JiraKeyUtil;
import jira.tool.ea.JiraProjectEnum;
import jira.tool.ea.JiraUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EACheckUtil {
    private static Date today = DateUtils.parse(DateUtils.format(new Date(), "yyyy-MM-dd"), "yyyy-MM-dd");

    public static String getLastMeetingDate() {
        return getLastMeetingDate(today);
    }

    public static String getLastMeetingDate(Date date) {
        if (date == null) {
            return null;
        }

        // Get the last meeting: Tuesday or Friday
        int day = DateUtils.dayOfWeek(date);
        if (day > Calendar.TUESDAY && day <= Calendar.FRIDAY) {
            day = day - Calendar.TUESDAY;
        } else {
            day = (day - Calendar.FRIDAY + 7) % 7;
        }
        return DateUtils.format(DateUtils.adjustDate(date, -day), "yyyyMMdd");
    }

    public static void formatDate(List<String[]> elementList) {
        if (elementList == null || elementList.size() <= 0) {
            return;
        }

        int rowStart = 0;
        int rowEnd = elementList.size() - 1;

        // Headers
        EAHeaderEnum.fillIndex(elementList.get(rowStart++));

        // Data
        int createIndex = EAHeaderEnum.CreatedDate.getIndex();
        int modifyIndex = EAHeaderEnum.ModifiedDate.getIndex();
        while (rowStart <= rowEnd) {
            String[] element = elementList.get(rowStart++);
            if (ArrayUtils.isEmpty(element)) {
                continue;
            }

            String strDate = element[createIndex];
            if (!StrUtils.isEmpty(strDate)) {
                element[createIndex] = EADateUtil.format(strDate, "yyyy-MM-dd HH:mm:ss");
            }

            strDate = element[modifyIndex];
            if (!StrUtils.isEmpty(strDate)) {
                element[modifyIndex] = EADateUtil.format(strDate, "yyyy-MM-dd HH:mm:ss");
            }
        }
    }

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
            if (!EADateUtil.needsToBeProcessed(element[EAHeaderEnum.CreatedDate.getIndex()], getLastMeetingDate())
                    && !EADateUtil.needsToBeProcessed(element[EAHeaderEnum.ModifiedDate.getIndex()], getLastMeetingDate())) {
                continue;
            }

            // Check if the estimation and due-date are valid
            if (StrUtils.isEmpty(EAEstimationUtil.processEstimation(element[EAHeaderEnum.Estimation.getIndex()]))
                    || StrUtils.isEmpty(EADateUtil.processDueDate(element[EAHeaderEnum.DueDate.getIndex()], today))) {
                continue;
            }

            // Check if the author and owner exist
            if (JiraUser.findUser(element[EAHeaderEnum.Author.getIndex()]) == null || JiraUser.findUser(element[EAHeaderEnum.Owner.getIndex()]) == null) {
                continue;
            }

            // TODO: Update QA

            // Valid
            if (teamElementListMap != null) {
                // Group as team
                String team = JiraUser.findUser(element[EAHeaderEnum.Owner.getIndex()]).getTeam();
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
}
