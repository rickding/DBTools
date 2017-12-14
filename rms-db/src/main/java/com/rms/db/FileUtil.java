package com.rms.db;

import com.rms.db.model.File;
import com.rms.db.model.Project;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {
    private static Map<String, File> nameItemMap = null;

    public static File getOrAdd(final Project project, final String path, final String name) {
        if (project == null || name == null || name.trim().length() <= 0 || path == null || path.length() <= 0) {
            return null;
        }

        if (nameItemMap == null) {
            nameItemMap = list2Map(DBUtil.getFileList());
        }

        // Find
        if (nameItemMap != null) {
            String nameStr = String.format("%d-%s-%s", project.getId(), path.trim().toLowerCase(), name.trim().toLowerCase());
            if (nameItemMap.containsKey(nameStr)) {
                return nameItemMap.get(nameStr);
            }
        }

        // Add new one
        File item = new File() {{
            setName(name.trim());
            setPath(path.trim());
            setProjectId(project.getId());
        }};

        item = DBUtil.addFile(item);
        if (item == null) {
            System.out.printf("Fail to add new file: %s, %s\r\n", name, project.getName());
        } else {
            System.out.printf("Add new file: %s, %s\r\n", name, project.getName());
            nameItemMap = null;
        }
        return item;
    }

    private static Map<String, File> list2Map(List<File> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<String, File> map = new HashMap<String, File>(list.size());
        for (File item : list) {
            map.put(String.format("%d-%s-%s", item.getProjectId(), item.getPath().trim().toLowerCase(), item.getName().trim().toLowerCase()), item);
        }
        return map;
    }
}
