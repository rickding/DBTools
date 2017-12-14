package com.rms.db;

import com.rms.db.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserUtil {
    private static Map<Long, User> userList2Map(List<User> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<Long, User> map = new HashMap<Long, User>(list.size());
        for (User item : list) {
            map.put(item.getId(), item);
        }
        return map;
    }
}
