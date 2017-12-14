package ea.tool.db;

import com.rms.db.ElementUtil;
import com.rms.db.FileUtil;
import com.rms.db.ProjectUtil;
import com.rms.db.model.Element;
import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.LongUtil;
import dbtools.common.utils.StrUtils;
import ea.tool.api.EAElementUtil;
import ea.tool.api.EAHeaderEnum;
import jira.tool.ea.JiraProjectEnum;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EA2DBUtil {
    public static void process(JiraProjectEnum project, String filePath, List<String[]> records) {
        if (project == null || StrUtils.isEmpty(filePath) || records == null || records.size() <= 0) {
            return;
        }

        // Existed elements from DB
        Map<String, Element> guidElementMap = ElementUtil.getGuidElementMap();
        if (guidElementMap == null) {
            guidElementMap = new HashMap<String, Element>();
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
            Map<String, Element> guidElementMap
    ) {
        if (project == null || StrUtils.isEmpty(filePath) || ArrayUtils.isEmpty(record) || keyRecordMap == null || guidElementMap == null) {
            return null;
        }

        // Check guid
        String guid = record[EAHeaderEnum.GUID.getIndex()];
        if (StrUtils.isEmpty(guid) || guidElementMap.containsKey(guid)) {
            return null;
        }

        // TODO: Check path, combined with name and project_id. Note: file_id can't be used since project has multiple files.

        // Convert and save to db
        Element element = elementFromRecord(project, filePath, record, keyRecordMap, guidElementMap);
        element = ElementUtil.addElement(element);
        if (element != null) {
            guidElementMap.put(guid, element);
        }
        return element;
    }

    private static Element elementFromRecord(
            JiraProjectEnum project, String filePath, String[] record, Map<String, String[]> keyRecordMap,
            Map<String, Element> guidElementMap
    ) {
        if (project == null || StrUtils.isEmpty(filePath) || ArrayUtils.isEmpty(record) || keyRecordMap == null || guidElementMap == null) {
            return null;
        }

        Element element = new Element();
        element.setProject(ProjectUtil.getOrAdd(project.getName(), project.getPrefixes(), project.getSubProject()));
        element.setFile(FileUtil.getOrAdd(element.getProject(), filePath, record[EAHeaderEnum.FileName.getIndex()]));
        element.setGuid(record[EAHeaderEnum.GUID.getIndex()]);

        element.setPath(record[EAHeaderEnum.ParentPath.getIndex()]);
        element.setName(record[EAHeaderEnum.Name.getIndex()]);
        element.setType(record[EAHeaderEnum.Type.getIndex()]);
        element.setStatus(record[EAHeaderEnum.Status.getIndex()]);

        element.setEstimation(LongUtil.valueOf(record[EAHeaderEnum.Estimation.getIndex()]));
        element.setDueDate(DateUtils.parse(record[EAHeaderEnum.DueDate.getIndex()]));
        element.setPd(record[EAHeaderEnum.Author.getIndex()]);
        element.setDev(record[EAHeaderEnum.Dev.getIndex()]);
        element.setQa(record[EAHeaderEnum.QA.getIndex()]);

        element.setEpic(record[EAHeaderEnum.Keywords.getIndex()]);
        element.setStory(record[EAHeaderEnum.JiraIssueKey.getIndex()]);
        element.setNotes(record[EAHeaderEnum.Notes.getIndex()]);
        element.setTag(record[EAHeaderEnum.Keywords.getIndex()]);

        // Find parent id
        String parentKey = record[EAHeaderEnum.ParentKey.getIndex()];
        if (!StrUtils.isEmpty(parentKey) && keyRecordMap.containsKey(parentKey)) {
            String[] parentRecord = keyRecordMap.get(parentKey);
            String parentGuid = parentRecord[EAHeaderEnum.GUID.getIndex()];
            if (!guidElementMap.containsKey(parentGuid)) {
                // Add parent element firstly
                addRecord(project, filePath, parentRecord, keyRecordMap, guidElementMap);
            }

            if (guidElementMap.containsKey(parentGuid)) {
                element.setParentId(guidElementMap.get(parentGuid).getId());
            } else {
                System.out.printf("Error: can't find parent element: %s\r\n", Arrays.asList(record));
            }
        }
        return element;
    }
}
