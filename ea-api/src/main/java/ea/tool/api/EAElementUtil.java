package ea.tool.api;

import org.sparx.Element;
import org.sparx.Package;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
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
        values.put(EAHeaderEnum.QA.getCode(), EAQAUtil.getQA(element.GetNotes()));

        values.put(EAHeaderEnum.JiraIssueKey.getCode(), element.GetStereotype());
        values.put(EAHeaderEnum.Status.getCode(), element.GetStatus());
        values.put(EAHeaderEnum.Notes.getCode(), element.GetNotes());

        values.put(EAHeaderEnum.Keywords.getCode(), element.GetTag());
        values.put(EAHeaderEnum.CreatedDate.getCode(), format(element.GetCreated(), EA_Date_Format));
        values.put(EAHeaderEnum.ModifiedDate.getCode(), format(element.GetModified(), EA_Date_Format));
        values.put(EAHeaderEnum.CreatedTime.getCode(), format(element.GetCreated(), EA_Time_Format));
        values.put(EAHeaderEnum.ModifiedTime.getCode(), format(element.GetModified(), EA_Time_Format));

        values.put(EAHeaderEnum.Key.getCode(), String.valueOf(element.GetElementID()));
        values.put(EAHeaderEnum.ParentKey.getCode(), String.valueOf(element.GetParentID()));
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

    private static String format(Date date, String format) {
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
}
