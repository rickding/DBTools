package com.rms.db;

import com.rms.db.model.Element;
import com.rms.db.model.ElementEx;
import com.rms.db.model.ElementGuid;
import com.rms.db.model.File;
import com.rms.db.model.JiraIssue;
import com.rms.db.model.Project;
import com.rms.db.model.Status;
import com.rms.db.model.Type;
import com.rms.db.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementUtil {
    private static Map<String, ElementEx> nameItemMap = null;
    private static Map<String, ElementEx> pathItemMap = new HashMap<String, ElementEx>();

    public static ElementEx addElement(final ElementEx item) {
        if (item == null || item.getName() == null || item.getName().trim().length() <= 0
                || item.getGuidList() == null || item.getGuidList().size() <= 0
                || item.getFileList() == null || item.getFileList().size() <= 0) {
            return null;
        }

        // Check guid
        Map<String, ElementEx> guidElementMap = getGuidElementMap();
        final String guid = item.getGuidList().get(0);
        if (guid == null || guid.trim().length() <= 0 || (guidElementMap != null && guidElementMap.containsKey(guid))) {
            return null;
        }

        // Check path, combined with name and project_id. Note: file_id can't be used since project has multiple files.
        final String path = getPath(item);
        if (path == null || path.trim().length() <= 0) {
            return null;
        }

        if (pathItemMap != null && pathItemMap.containsKey(path)) {
            final File file = item.getFileList().get(0);
            final ElementEx existedItem = pathItemMap.get(path);
            System.out.printf("Duplicated elements: %s, %s, %s\r\n",
                    path,
                    file.getPath(),
                    existedItem.getFileList() == null || existedItem.getFileList().size() <= 0 ? "existed file" : existedItem.getFileList().get(0).getPath());

            // Add new Guid
            DBUtil.addElementGuid(new ElementGuid(){{
                setElementId(existedItem.getId());
                setGuid(guid);
                setFileId(file.getId());
            }});

            existedItem.setGuidList(guid);
            existedItem.setFileList(file);
            guidElementMap.put(guid, existedItem);

            item.setId(existedItem.getId());
            return item;
        }

        // TODO: More information to check the duplicated elements(story)

        // Type
        Type type = TypeUtil.findOrAdd(item.getTypeName());
        if (type == null) {
            System.out.printf("Fail to add new type: %s, %s\r\n", item.getTypeName(), item.getName());
            return null;
        }
        item.setTypeId(type.getId());

        // Status
        Status status = StatusUtil.getOrAdd(item.getStatusName());
        if (status != null) {
            item.setStatusId(status.getId());
        }

        // Jira issue
        JiraIssue story = JiraIssueUtil.findOrAdd(item.getStoryKey(), item.getStoryName(), item.getStoryGuid());
        if (story != null) {
            item.setStoryId(story.getId());
        }
        story = JiraIssueUtil.findOrAdd(item.getEpicKey(), item.getEpicName(), item.getEpicGuid());
        if (story != null) {
            item.setEpicId(story.getId());
        }

        // Users
        User user = UserUtil.findOrAdd(item.getPdName());
        if (user != null) {
            item.setPdId(user.getId());
        }
        user = UserUtil.findOrAdd(item.getDevName());
        if (user != null) {
            item.setDevId(user.getId());
        }
        user = UserUtil.findOrAdd(item.getQaName());
        if (user != null) {
            item.setQaId(user.getId());
        }

        // Save element
        ElementEx tmp =  DBUtil.addElement(item);
        if (tmp != null && guidElementMap != null) {
            guidElementMap.put(guid, tmp);
            pathItemMap.put(getPath(item), item);
        }
        return tmp;
    }

    public static Map<String, ElementEx> getGuidElementMap() {
        synchronized ("getGuidElementMap") {
            if (nameItemMap == null) {
                nameItemMap = list2Map(DBUtil.getElementList(), pathItemMap, null);
            }
        }
        return nameItemMap;
    }

    private static String getPath(Element item) {
        if (item == null) {
            return null;
        }
        return String.format("%d_%s_%s",
                item.getProjectId(),
                item.getPath() == null ? "x" : item.getPath().trim().toLowerCase(),
                item.getName() == null ? "x" : item.getName().trim().toLowerCase()
        );
    }

    public static void updateElementInfo(List<ElementEx> elementList, List<Project> projectList, List<ElementGuid> guidList, List<File> fileList) {
        if (elementList == null || elementList.size() <= 0) {
            return;
        }

        // Project
        Map<Long, Project> idProjectMap = new HashMap<Long, Project>();
        ProjectUtil.list2Map(projectList, idProjectMap);

        for (ElementEx item : elementList) {
            if (idProjectMap != null && idProjectMap.containsKey(item.getProjectId())) {
                item.setProject(idProjectMap.get(item.getProjectId()));
            }
        }

        // Multiple guid and files
        Map<Long, ElementEx> idElementMap = new HashMap<Long, ElementEx>();
        list2Map(elementList, null, idElementMap);

        Map<Long, File> idFileMap = new HashMap<Long, File>();
        FileUtil.list2Map(fileList, idFileMap);

        if (guidList != null && guidList.size() > 0) {
            for (ElementGuid item : guidList) {
                ElementEx element = idElementMap.get(item.getElementId());
                if (element != null) {
                    element.setGuidList(item.getGuid());
                    element.setFileList(idFileMap.get(item.getFileId()));
                }
            }
        }
    }

    private static Map<String, ElementEx> list2Map(List<ElementEx> list, Map<String, ElementEx> pathItemMap, Map<Long, ElementEx> idItemMap) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<String, ElementEx> map = new HashMap<String, ElementEx>(list.size());
        for (ElementEx item : list) {
            for (String guid : item.getGuidList()) {
                map.put(guid, item);
            }

            if (pathItemMap != null) {
                pathItemMap.put(getPath(item), item);
            }

            if (idItemMap != null) {
                idItemMap.put(item.getId(), item);
            }
        }
        return map;
    }
}
