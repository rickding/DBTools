package ea.tool.db;

import com.rms.db.model.ElementEx;
import dbtools.common.HttpClientUtil;
import dbtools.common.JsonUtil;

import java.util.HashMap;
import java.util.Map;

public class RMSUtil {
    private static String url = "http://localhost:1337/parse/classes/element";
    private static Map<String, String> headers = new HashMap<String, String>() {{
        put("X-Parse-Application-Id", "myAppId");
        put("X-Parse-Master-Key", "myMasterKey");
    }};

    public static String addElement(ElementEx element) {
        if (element == null) {
            return null;
        }
        return postJson(JsonUtil.toJson(element));
    }

    public static String postJson(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().length() <= 0) {
            return null;
        }
        return HttpClientUtil.sendHttpPostJson(url, headers, jsonStr.trim());
    }
}
