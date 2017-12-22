package jira.tool.report;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dbtools.common.HttpClientUtil;
import dbtools.common.JsonUtil;
import dbtools.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RMSUtil {
    public static String postReport(final String name, final String date, final String duration, final List<Map<String, String>> data) {
        if (StrUtils.isEmpty(name) || StrUtils.isEmpty(date) || StrUtils.isEmpty(duration) || data == null || data.size() <= 0) {
            return null;
        }

        // get
        JSONArray items = get();
        if (items != null && items.size() > 0) {
            List<String> idList = new ArrayList<String>();
            for (Object item : items) {
                JSONObject jsonObj = (JSONObject) item;
                if (name.equalsIgnoreCase(jsonObj.getString("name"))
                        && date.equalsIgnoreCase(jsonObj.getString("date"))
                        && duration.equalsIgnoreCase(jsonObj.getString("duration"))) {
                    idList.add(jsonObj.getString("objectId"));
                }
            }

            // delete
            if (idList != null && idList.size() > 0) {
                delete(idList);
            }
        }

        // post
        Map<String, Object> report = new HashMap<String, Object>() {{
            put("name", name);
            put("date", date);
            put("duration", duration);
            put("data", data);
        }};
        return post(JsonUtil.toString(report));
    }

    private static String classPath = "/parse/classes/report";
    private static String classUrl = String.format("%s%s", "http://localhost:1337", classPath);
    private static String batchUrl = "http://localhost:1337/parse/batch";

    private static Map<String, String> headers = new HashMap<String, String>() {{
        put("X-Parse-Application-Id", "myAppId");
        put("X-Parse-Master-Key", "myMasterKey");
    }};

    public static String post(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().length() <= 0) {
            return null;
        }
        return HttpClientUtil.sendHttpPostJson(classUrl, headers, jsonStr.trim());
    }

    public static String delete(List<String> items) {
        if (items == null || items.size() <= 0) {
            return null;
        }

        final List<Map<String, String>> operationList = new ArrayList<Map<String, String>>();
        for (String item : items) {
            final String itemId = item;
            Map<String, String> operation = new HashMap<String, String>() {{
                put("method", "DELETE");
                put("path", String.format("%s/%s", classPath, itemId));
            }};
            operationList.add(operation);
        }

        String jsonStr = JsonUtil.toString(new HashMap<String, Object>() {{
            put("requests", operationList);
        }});
        return HttpClientUtil.sendHttpPostJson(batchUrl, headers, jsonStr);
    }

    public static JSONArray get() {
        String jsonStr = HttpClientUtil.sendHttpGet(classUrl, headers);
        Object jsonObj = JsonUtil.toObject(jsonStr);
        if (jsonObj == null) {
            return null;
        }

        Map<String, JSONArray> jsonMap = (Map<String, JSONArray>)jsonObj;
        if (jsonMap == null) {
            return null;
        }

        JSONArray jsonArray = jsonMap.get("results");
        if (jsonArray == null || jsonArray.size() <= 0) {
            return null;
        }
        return jsonArray;
    }
}
