package com.rms.db;

import com.rms.db.model.Status;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatusUtil {
    private static Map<String, Status> nameItemMap = null;

    public static Status getOrAdd(final String name) {
        if (name == null || name.trim().length() <= 0) {
            return null;
        }

        if (nameItemMap == null) {
            nameItemMap = list2Map(DBUtil.getStatusList());
        }

        // Find
        if (nameItemMap != null) {
            String nameStr = name.trim().toLowerCase();
            if (nameItemMap.containsKey(nameStr)) {
                return nameItemMap.get(nameStr);
            }

            for (Status item : nameItemMap.values()) {
                if (inInAlias(item.getAliases(), nameStr)) {
                    return item;
                }
            }
        }

        // Add new one
        Status item = DBUtil.addStatus(new Status() {{
            setName(name.trim());
        }});

        if (item == null) {
            System.out.printf("Fail to add new status: %s\r\n", name);
        } else {
            System.out.printf("Add new status: %s\r\n", name);
            nameItemMap = null;
        }
        return item;
    }

    public static boolean inInAlias(String aliases, String name) {
        if (aliases == null || aliases.trim().length() <= 0 || name == null || name.length() <= 0) {
            return false;
        }

        String[] aliasArray = aliases.split(",");
        if (aliasArray != null && aliasArray.length > 0) {
            name = name.trim();
            for (String alias : aliasArray) {
                if (name.equalsIgnoreCase(alias.trim())) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String aliasesToString(String[] aliases) {
        if (aliases == null || aliases.length <= 0) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        for (String alias : aliases) {
            sb.append(",");
            sb.append(alias.trim());
        }

        if (sb.length() > 0) {
            return sb.substring(1);
        }
        return null;
    }

    private static Map<String, Status> list2Map(List<Status> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<String, Status> map = new HashMap<String, Status>(list.size());
        for (Status item : list) {
            map.put(item.getName().trim().toLowerCase(), item);
        }
        return map;
    }
}
