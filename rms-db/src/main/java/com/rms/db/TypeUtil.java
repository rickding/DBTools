package com.rms.db;

import com.rms.db.model.Type;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TypeUtil {
    private static Map<String, Type> nameItemMap = null;

    public static Type findOrAdd(final String name) {
        if (name == null || name.trim().length() <= 0) {
            return null;
        }

        if (nameItemMap == null) {
            nameItemMap = list2Map(DBUtil.getTypeList());
        }

        // Find
        if (nameItemMap != null) {
            String nameStr = name.trim().toLowerCase();
            if (nameItemMap.containsKey(nameStr)) {
                return nameItemMap.get(nameStr);
            }
        }

        // Add new one
        Type item = DBUtil.addType(new Type() {{
            setName(name.trim());
        }});

        if (item == null) {
            System.out.printf("Fail to add new type: %s\r\n", name);
        } else {
            System.out.printf("Add new type: %s\r\n", name);
            nameItemMap = null;
        }
        return item;
    }

    private static Map<String, Type> list2Map(List<Type> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<String, Type> map = new HashMap<String, Type>(list.size());
        for (Type item : list) {
            map.put(item.getName().trim().toLowerCase(), item);
        }
        return map;
    }
}
