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

        values.put(EAHeaderEnum.Key.getCode(), String.valueOf(element.GetElementID()));
        values.put(EAHeaderEnum.ParentKey.getCode(), String.valueOf(element.GetParentID() > 0 ? element.GetParentID() : element.GetPackageID()));
        return values;
    }

    private static Map<String, String> formatPackage(final Package pack) {
        if (pack == null) {
            return null;
        }
        return new HashMap<String, String>() {{
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

            put(EAHeaderEnum.Key.getCode(), String.valueOf(pack.GetPackageID()));
            put(EAHeaderEnum.ParentKey.getCode(), String.valueOf(pack.GetParentID()));
        }};
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
        while (parentKey != null && parentKey.trim().length() > 0) {
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
        int nameIndex = EAHeaderEnum.Name.getIndex();

        // Generate the map: key to parent elements
        Map<String, String[]> keyElementMap = new HashMap<String, String[]>(elements.size());
        for (int i = startIndex; i < elements.size(); i++) {
            String[] element = elements.get(i);
            if (element == null || element.length <= 0 || keyIndex < 0 || keyIndex >= element.length) {
                continue;
            }

            if (ignoreFirstModel && "Model".equalsIgnoreCase(element[nameIndex])) {
                continue;
            }

            String key = element[keyIndex];
            if (key != null && key.trim().length() > 0) {
                keyElementMap.put(key, element);
            }
        }

        return keyElementMap;
    }
}
