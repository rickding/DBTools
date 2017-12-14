package dbtools.common.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {
    private static <T> Map<Long, T> toMap(List<T> list) {
        if (list == null || list.size() <= 0) {
            return null;
        }

        Map<Long, T> map = new HashMap<Long, T>(list.size());
        for (T item : list) {
//            map.put(item.getId(), item);
        }
        return map;
    }
}
