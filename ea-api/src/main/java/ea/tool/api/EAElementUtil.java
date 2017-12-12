package ea.tool.api;

import org.sparx.Element;
import org.sparx.Package;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EAElementUtil {
    private static String EA_Date_Format = "yyyy-MM-dd";
    private static String EA_Time_Format = "HH:mm:ss";
    public static String Root_Model_Key = "P_0";

    public static String[] getValues(Element element) {
        return getValues(formatElement(element));
    }

    public static String[] getValues(Package pack) {
        return getValues(formatPackage(pack));
    }

    private static String[] getValues(Map<String, String> valueMap) {
        if (valueMap == null || valueMap.size() <= 0) {
            return null;
        }

        String[] headers = EAHeaderEnum.getHeaders();
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            headers[i] = valueMap.containsKey(header) ? valueMap.get(header) : null;
        }
        return headers;
    }

    private static Map<String, String> formatElement(Element element) {
        if (element == null) {
            return null;
        }

        Map<String, String> values = new HashMap<String, String>();
        values.put(EAHeaderEnum.GUID.getCode(), element.GetElementGUID());
        values.put(EAHeaderEnum.Type.getCode(), element.GetType());
        values.put(EAHeaderEnum.Name.getCode(), element.GetName());

        values.put(EAHeaderEnum.Author.getCode(), element.GetAuthor());
        values.put(EAHeaderEnum.Estimation.getCode(), element.GetVersion());
        values.put(EAHeaderEnum.DueDate.getCode(), element.GetPhase());
        values.put(EAHeaderEnum.Dev.getCode(), element.GetAlias());
        values.put(EAHeaderEnum.QA.getCode(), EAQAUtil.getQAStr(element.GetNotes()));

        values.put(EAHeaderEnum.JiraIssueKey.getCode(), element.GetStereotype());
        values.put(EAHeaderEnum.Status.getCode(), element.GetStatus());
        values.put(EAHeaderEnum.Notes.getCode(), element.GetNotes());

        values.put(EAHeaderEnum.Keywords.getCode(), element.GetTag());
        values.put(EAHeaderEnum.CreatedDate.getCode(), format(element.GetCreated(), EA_Date_Format));
        values.put(EAHeaderEnum.ModifiedDate.getCode(), format(element.GetModified(), EA_Date_Format));
        values.put(EAHeaderEnum.CreatedTime.getCode(), format(element.GetCreated(), EA_Time_Format));
        values.put(EAHeaderEnum.ModifiedTime.getCode(), format(element.GetModified(), EA_Time_Format));

        values.put(EAHeaderEnum.Key.getCode(), String.format("E_%d", element.GetElementID()));

        int pId = element.GetParentID() > 0 ? element.GetParentID() : element.GetPackageID();
        if (element.GetParentID() > 0 && pId == element.GetElementID()) {
            System.out.printf("Error: the same parent package or element Id: %d, %s\r\n", pId, element.GetName());
            pId = 0;
        }

        values.put(EAHeaderEnum.ParentKey.getCode(), String.format("%s_%d", element.GetParentID() > 0 ? "E" : "P", pId));
        return values;
    }

    private static Map<String, String> formatPackage(final Package pack) {
        if (pack == null) {
            return null;
        }

        Map<String, String> values = new HashMap<String, String>() {{
            put(EAHeaderEnum.GUID.getCode(), pack.GetPackageGUID());
            put(EAHeaderEnum.Type.getCode(), "Package");
            put(EAHeaderEnum.Name.getCode(), pack.GetName());

            put(EAHeaderEnum.Author.getCode(), pack.GetOwner());
            put(EAHeaderEnum.Estimation.getCode(), pack.GetVersion());
            put(EAHeaderEnum.DueDate.getCode(), null);
            put(EAHeaderEnum.Dev.getCode(), pack.GetAlias());

            put(EAHeaderEnum.JiraIssueKey.getCode(), null);
            put(EAHeaderEnum.Status.getCode(), null);
            put(EAHeaderEnum.Notes.getCode(), pack.GetNotes());

            put(EAHeaderEnum.Keywords.getCode(), null);
            put(EAHeaderEnum.CreatedDate.getCode(), format(pack.GetCreated(), EA_Date_Format));
            put(EAHeaderEnum.ModifiedDate.getCode(), format(pack.GetModified(), EA_Date_Format));

            put(EAHeaderEnum.Key.getCode(), String.format("P_%d", pack.GetPackageID()));
        }};

        int pId = pack.GetParentID();
        if (pId == pack.GetPackageID()) {
            System.out.printf("Error: the same parent package Id: %d, %s\r\n", pId, pack.GetName());
            pId = 0;
        }

        values.put(EAHeaderEnum.ParentKey.getCode(), String.format("P_%d", pId));
        return values;
    }

    public static String format(Date date, String format) {
        if (date == null || format == null || format.trim().length() <= 0) {
            return "";
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            System.out.printf("%s, %s, %s\r\n", e.getMessage(), date, format);
        }
        return "";
    }

    public static String getParentPath(String[] element, Map<String, String[]> keyElementMap) {
        return getParentPath(element, keyElementMap, -1, -1);
    }

    public static String getParentPath(String[] element, Map<String, String[]> keyElementMap, int level, int maxLength) {
        if (element == null || element.length <= 0 || keyElementMap == null || keyElementMap.size() <= 0) {
            return null;
        }

        int count = 0;
        String parentKey = element[EAHeaderEnum.ParentKey.getIndex()];
        StringBuilder sb = new StringBuilder();
        while (parentKey != null && parentKey.trim().length() > 0 && !parentKey.trim().equalsIgnoreCase(Root_Model_Key)) {
            count++;
            if (level >= 0 && count > level) {
                break;
            }

            String[] parentElement = keyElementMap.get(parentKey);
            if (parentElement == null || parentElement.length <= 0) {
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
        return getKeyElementMap(elements, false);
    }

    public static Map<String, String[]> getKeyElementMap(List<String[]> elements, boolean ignoreFirstModel) {
        if (elements == null || elements.size() <= 0) {
            return null;
        }

        // headers at first line
        int startIndex = 1;
        int keyIndex = EAHeaderEnum.Key.getIndex();

        // Generate the map: key to parent elements
        Map<String, String[]> keyElementMap = new HashMap<String, String[]>(elements.size());
        for (int i = startIndex; i < elements.size(); i++) {
            String[] element = elements.get(i);
            if (element == null || element.length <= 0 || keyIndex < 0 || keyIndex >= element.length) {
                continue;
            }

            String key = element[keyIndex];
            if (ignoreFirstModel && Root_Model_Key.equalsIgnoreCase(key)) {
                continue;
            }

            if (key != null && key.trim().length() > 0) {
                keyElementMap.put(key, element);
            }
        }
        return keyElementMap;
    }

    public static int countRequirements(List<String[]> elements, boolean checkStoryKey) {
        if (elements == null || elements.size() <= 0) {
            return 0;
        }

        int count = 0;
        int typeIndex = EAHeaderEnum.Type.getIndex();
        for (String[] element : elements) {
            if (element == null || element.length <= 0 || typeIndex < 0 || typeIndex >= element.length) {
                continue;
            }

            String type = element[typeIndex];
            if (type != null && type.trim().length() > 0 && EATypeEnum.Requirement.getCode().equalsIgnoreCase(type)) {
                // check stereotype
                String key = element[EAHeaderEnum.JiraIssueKey.getIndex()];
                if (checkStoryKey && (key == null || key.trim().length() <= 0)) {
                    continue;
                }
                count++;
            }
        }
        return count;
    }
}
