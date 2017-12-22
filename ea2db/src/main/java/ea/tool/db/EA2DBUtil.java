package ea.tool.db;

import com.rms.db.ElementUtil;
import com.rms.db.FileUtil;
import com.rms.db.ProjectUtil;
import com.rms.db.model.Element;
import com.rms.db.model.ElementEx;
import com.rms.db.model.Project;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.LongUtil;
import dbtools.common.utils.StrUtils;
import ea.tool.api.EAElementUtil;
import ea.tool.api.EAEstimationUtil;
import ea.tool.api.EAHeaderEnum;
import jira.tool.db.JiraKeyUtil;
import jira.tool.db.JiraStoryUtil;
import jira.tool.db.JiraUser;
import jira.tool.db.model.Story;
import jira.tool.ea.EA2Jira;
import jira.tool.ea.EADateUtil;
import jira.tool.ea.JiraEpicUtil;
import jira.tool.ea.JiraProjectEnum;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EA2DBUtil {
    public static void process(JiraProjectEnum project, String filePath, List<String[]> records) {
        if (project == null || StrUtils.isEmpty(filePath) || records == null || records.size() <= 0) {
            return;
        }

        // Existed elements from DB
        Map<String, ElementEx> guidElementMap = ElementUtil.getGuidElementMap();
        if (guidElementMap == null) {
            guidElementMap = new HashMap<String, ElementEx>();
        }

        // Headers
        int rowStart = 0;
        EAHeaderEnum.fillIndex(records.get(rowStart++));
        Map<String, String[]> keyRecordMap = EAElementUtil.getKeyElementMap(records);

        // save to db
        for (; rowStart < records.size(); rowStart++) {
            String[] values = records.get(rowStart);
            addRecord(project, filePath, values, keyRecordMap, guidElementMap);
        }
    }

    public static Element addRecord(
            JiraProjectEnum project, String filePath, String[] record, Map<String, String[]> keyRecordMap,
            Map<String, ElementEx> guidElementMap
    ) {
        if (project == null || StrUtils.isEmpty(filePath) || ArrayUtils.isEmpty(record) || keyRecordMap == null || guidElementMap == null) {
            return null;
        }

        // Convert and save to db
        ElementEx element = elementFromRecord(project, filePath, record, keyRecordMap, guidElementMap);
        element = ElementUtil.addElement(element);
        if (element != null) {
            String guid = record[EAHeaderEnum.GUID.getIndex()];
            if (guid != null && guid.trim().length() > 0) {
                guidElementMap.put(guid.trim().toUpperCase(), element);
            }
        }

        // Save to rms server: parse-server
        RMSUtil.addElement(element);
        return element;
    }

    private static ElementEx elementFromRecord(
            JiraProjectEnum project, String filePath, String[] record, Map<String, String[]> keyRecordMap,
            Map<String, ElementEx> guidElementMap
    ) {
        if (project == null || StrUtils.isEmpty(filePath) || ArrayUtils.isEmpty(record) || keyRecordMap == null || guidElementMap == null) {
            return null;
        }

        // Project
        ElementEx element = new ElementEx();
        Project prj = ProjectUtil.getOrAdd(project.getName(), project.getPrefixes(), project.getSubProject());
        element.setProject(prj);
        element.setProjectId(prj.getId());

        // File, guid, path
        element.setFileList(FileUtil.getOrAdd(prj, filePath, record[EAHeaderEnum.FileName.getIndex()]));
        element.setGuidList(record[EAHeaderEnum.GUID.getIndex()]);
        element.setPath(record[EAHeaderEnum.ParentPath.getIndex()]);

        element.setName(record[EAHeaderEnum.Name.getIndex()]);
        element.setNotes(record[EAHeaderEnum.Notes.getIndex()]);
        element.setTag(record[EAHeaderEnum.Keywords.getIndex()]);

        // Estimation
        String estimation = EAEstimationUtil.processEstimation(record[EAHeaderEnum.Estimation.getIndex()], false);
        if (!StrUtils.isEmpty(estimation)) {
            element.setEstimation(LongUtil.valueOf(estimation));
        }

        // Due date, qa and pd
        String date = EADateUtil.processDueDate(record[EAHeaderEnum.CreatedDate.getIndex()]);
        Date today = DateUtils.parse(date, EA2Jira.Jira_Date_Format);
        date = EADateUtil.processDueDate(record[EAHeaderEnum.DueDate.getIndex()], today);
        if (!StrUtils.isEmpty(date)) {
            element.setDueDate(DateUtils.parse(date, EA2Jira.Jira_Date_Format));
            element.setQaDate(DateUtils.adjustDate(element.getDueDate(), -2));

            date = EADateUtil.processDueDate(record[EAHeaderEnum.ModifiedDate.getIndex()], today);
            if (!StrUtils.isEmpty(date)) {
                element.setPdDate(DateUtils.parse(date, EA2Jira.Jira_Date_Format));
            }
        }

        // Type and status
        element.setTypeName(record[EAHeaderEnum.Type.getIndex()]);
        element.setStatusName(record[EAHeaderEnum.Status.getIndex()]);

        // Pd, dev, QA
        JiraUser user = JiraUser.findUser(record[EAHeaderEnum.Author.getIndex()]);
        if (user != null) {
            element.setPdName(user.getName());
        }

        user = JiraUser.findUser(record[EAHeaderEnum.Dev.getIndex()]);
        if (user != null) {
            element.setDevName(user.getName());
        }

        user = JiraUser.findUser(record[EAHeaderEnum.QA.getIndex()]);
        if (user != null) {
            element.setQaName(user.getName());
        }

        // Jira story and epic
        Story story = JiraStoryUtil.findStory(record[EAHeaderEnum.JiraIssueKey.getIndex()]);
        if (story != null) {
            element.setStoryKey(story.getKey());
            element.setStoryName(story.getTitle());
            element.setStoryGuid(story.getEAGUID());
        }

        Story epic = JiraEpicUtil.findEpic(record[EAHeaderEnum.Keywords.getIndex()]);
        if (epic != null) {
            element.setEpicKey(epic.getKey());
            element.setEpicName(epic.getTitle());
            element.setEpicGuid(epic.getEAGUID());
        }

        // Find parent id
        String parentKey = record[EAHeaderEnum.ParentKey.getIndex()];
        if (!StrUtils.isEmpty(parentKey) && keyRecordMap.containsKey(parentKey)) {
            String[] parentRecord = keyRecordMap.get(parentKey);
            String parentGuid = parentRecord[EAHeaderEnum.GUID.getIndex()];
            if (parentGuid != null && parentGuid.trim().length() > 0) {
                parentGuid = parentGuid.trim().toUpperCase();
                if (!guidElementMap.containsKey(parentGuid)) {
                    // Add parent element firstly
                    addRecord(project, filePath, parentRecord, keyRecordMap, guidElementMap);
                }

                if (guidElementMap.containsKey(parentGuid)) {
                    element.setParentId(guidElementMap.get(parentGuid).getId());
                } else {
                    System.out.printf("Error: can't find parent element: %s, %s\r\n", parentGuid, Arrays.asList(record));
                }
            }
        }
        return element;
    }
}
