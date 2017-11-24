package jira.tool.ea;

import dbtools.common.utils.ArrayUtils;
import dbtools.common.utils.StrUtils;

import java.util.*;

public class EAElementUtil {
    public static String getParentPath(String[] element, Map<String, String[]> keyElementMap) {
        return getParentPath(element, keyElementMap, -1, -1);
    }
    public static String getParentPath(String[] element, Map<String, String[]> keyElementMap, int level, int maxLength) {
        if (ArrayUtils.isEmpty(element) || keyElementMap == null || keyElementMap.size() <= 0) {
            return null;
        }

        int count = 0;
        String parentKey = element[EAHeaderEnum.ParentKey.getIndex()];
        StringBuilder sb = new StringBuilder();
        while (!StrUtils.isEmpty(parentKey)) {
            count++;
            if (level >= 0 && count > level) {
                break;
            }

            String[] parentElement = keyElementMap.get(parentKey);
            if (ArrayUtils.isEmpty(parentElement)) {
                break;
            }


            String strParent = parentElement[EAHeaderEnum.Name.getIndex()];
            if (maxLength >= 0 && sb.length() + strParent.length() > maxLength) {
                break;
            }

            sb.insert(0, strParent);
            sb.insert(0, "-");

            parentKey = parentElement[EAHeaderEnum.ParentKey.getIndex()];
        }

        if (sb.length() > 0) {
            return sb.substring(1);
        }
        return null;
    }

    public static Map<String, String[]> getKeyElementMap(List<String[]> elements) {
        if (elements == null || elements.size() <= 0) {
            return null;
        }

        // headers at first line
        int startIndex = 1;

        // Generate the map: key to parent elements
        Map<String, String[]> keyElementMap = new HashMap<String, String[]>(elements.size());
        for (int i = startIndex; i < elements.size(); i++) {
            String[] element = elements.get(i);
            String key = element[EAHeaderEnum.Key.getIndex()];
            if (!StrUtils.isEmpty(key)) {
                keyElementMap.put(key, element);
            }
        }

        return keyElementMap;
    }

    public static Set<String> getKeySet(List<String[]> elements, int index) {
        if (elements == null || elements.size() <= 0) {
            return null;
        }

        // headers at first line
        int startIndex = 1;

        // Find the parent key, map key to children elements
        Set<String> keySet = new HashSet<String>(elements.size());

        for (int i = startIndex; i < elements.size(); i++) {
            String[] element = elements.get(i);
            if (!ArrayUtils.isEmpty(element) && index >= 0 && index < element.length) {
                String key = element[index];
                if (!StrUtils.isEmpty(key)) {
                    keySet.add(key);
                }
            }
        }

        return keySet;
    }
}
