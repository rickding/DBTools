package com.rms.db;

import com.rms.db.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserUtil {
    private static Map<String, User> nameItemMap = null;

    public static User findOrAdd(final String name) {
        if (name == null || name.trim().length() <= 0) {
            return null;
        }

        if (nameItemMap == null) {
            nameItemMap = list2Map(DBUtil.getUserList());
        }

        // Find
        if (nameItemMap != null) {
            String nameStr = name.trim().toLowerCase();
            if (nameItemMap.containsKey(nameStr)) {
                return nameItemMap.get(nameStr);
            }

            for (User item : nameItemMap.values()) {
                if (StatusUtil.inInAlias(item.getAliases(), nameStr)) {
                    return item;
                }
            }
        }

        // Add new one
        User item = DBUtil.addUser(new User() {{
            setName(name.trim());
        }});

        if (item == null) {
            System.out.printf("Fail to add new user: %s\r\n", name);
        } else {
            System.out.printf("Add new user: %s\r\n", name);
            nameItemMap = null;
        }
        return item;
    }

    private static Map<String, User> list2Map(List<User> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<String, User> map = new HashMap<String, User>(list.size());
        for (User item : list) {
            map.put(item.getName().trim().toLowerCase(), item);
        }
        return map;
    }
}
