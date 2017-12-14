package com.rms.db;

import com.rms.db.model.Element;
import com.rms.db.model.Status;
import com.rms.db.model.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ElementUtil {
    private static Map<String, Element> nameItemMap = null;

    public static Element addElement(final Element item) {
        if (item == null || item.getName() == null || item.getName().trim().length() <= 0) {
            return null;
        }

        // Check guid
        Map<String, Element> guidElementMap = getGuidElementMap();
        String guid = item.getGuid();
        if (guid == null || guid.trim().length() <= 0 || (guidElementMap != null && guidElementMap.containsKey(guid))) {
            return null;
        }

        // Type
        Type type = TypeUtil.findOrAdd(item.getType());
        if (type == null) {
            System.out.printf("Fail to add new type: %s, %s\r\n", item.getType(), item.getName());
            return null;
        }
        item.setTypeId(type.getId());

        // Status
        Status status = StatusUtil.getOrAdd(item.getStatus());
        if (status != null) {
            item.setStatusId(status.getId());
        }

        // Verify and fill the necessary information: users(pd, dev, qa), jira-issue, file, project, estimation, date

        Element tmp =  DBUtil.addElement(item);
        if (tmp != null && guidElementMap != null) {
            guidElementMap.put(tmp.getGuid(), tmp);
        }
        return tmp;
    }

    public static Map<String, Element> getGuidElementMap() {
        synchronized ("getGuidElementMap") {
            if (nameItemMap == null) {
                nameItemMap = list2Map(DBUtil.getElementList());
            }
        }
        return nameItemMap;
    }

    private static Map<String, Element> list2Map(List<Element> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<String, Element> map = new HashMap<String, Element>(list.size());
        for (Element item : list) {
            map.put(item.getGuid(), item);
        }
        return map;
    }
}
