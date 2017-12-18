package com.rms.db;

import com.rms.db.model.Project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectUtil {
    private static Map<String, Project> nameItemMap = null;

    public static Project getOrAdd(final String name, String[] aliases, final String subProject) {
        if (name == null || name.trim().length() <= 0) {
            return null;
        }

        if (nameItemMap == null) {
            nameItemMap = list2Map(DBUtil.getProjectList(), null);
        }

        // Find
        if (nameItemMap != null) {
            String nameStr = String.format("%s_%s",
                    name.trim().toLowerCase(),
                    subProject == null ? "" : subProject.trim().toLowerCase());

            if (nameItemMap.containsKey(nameStr)) {
                return nameItemMap.get(nameStr);
            }

            for (Project item : nameItemMap.values()) {
                if (StatusUtil.inInAlias(item.getAliases(), nameStr) && ((subProject == null && item.getSubProject() == null) || subProject.trim().equalsIgnoreCase(item.getSubProject().trim()))) {
                    return item;
                }
            }
        }

        // Add new one
        Project item = new Project() {{
            setName(name.trim());
        }};
        if (aliases != null && aliases.length > 0) {
            item.setAliases(StatusUtil.aliasesToString(aliases));
        }
        if (subProject != null && subProject.trim().length() > 0) {
            item.setSubProject(subProject.trim());
        }

        item = DBUtil.addProject(item);
        if (item == null) {
            System.out.printf("Fail to add new project: %s, %s\r\n", name, Arrays.asList(aliases));
        } else {
            System.out.printf("Add new project: %s, %s\r\n", name, Arrays.asList(aliases));
            nameItemMap = null;
        }
        return item;
    }

    public static Map<String, Project> list2Map(List<Project> list, Map<Long, Project> idItemMap) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<String, Project> map = new HashMap<String, Project>(list.size());
        for (Project item : list) {
            map.put(String.format("%s_%s",
                    item.getName().trim().toLowerCase(),
                    item.getSubProject() == null ? "" : item.getSubProject().trim().toLowerCase()
            ), item);

            if (idItemMap != null) {
                idItemMap.put(item.getId(), item);
            }
        }
        return map;
    }
}
