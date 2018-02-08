package jira.tool.report;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dbtools.common.HttpClientUtil;
import dbtools.common.JsonUtil;
import dbtools.common.utils.DateUtils;
import dbtools.common.utils.StrUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RMSUtil {
    public static String postReportData(final String name, final String date, final String duration, final Object data) {
        return null; // postReport(name, date, duration, data, true);
    }

    public static String postReport(final String name, final String date, final String duration, final Object data) {
        return null; // postReport(name, date, duration, data, false);
    }

    private static String postReport(final String name, final String date, final String duration, final Object data, boolean isRawData) {
        isPostingRawData = isRawData;
        if (StrUtils.isEmpty(name) || StrUtils.isEmpty(date) || StrUtils.isEmpty(duration) || data == null) {
            return null;
        }

        // get
        JSONArray items = get();
        if (items != null && items.size() > 0) {
            List<String> idList = new ArrayList<String>();
            for (Object item : items) {
                JSONObject jsonObj = (JSONObject) item;
                if (name.equalsIgnoreCase(jsonObj.getString("name"))
//                        && date.equalsIgnoreCase(jsonObj.getString("date"))
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
        return post(name, JsonUtil.toString(report));
    }

    // http://docs.parseplatform.org/rest/guide/
//    localhost:1337/parse/classes/report?where={"name": "人天交付运营能力_本周交付统计"}
//    private static String baseUrl = "http://localhost:1337";
    private static String baseUrl = "http://192.168.20.161:1337";

    private static boolean isPostingRawData = false;
    private static Map<String, String> headers = new HashMap<String, String>() {{
        put("X-Parse-Application-Id", "myAppId");
        put("X-Parse-Master-Key", "myMasterKey");
    }};

    private static String getClassPath() {
        return String.format("/parse/classes/report%s%s", isPostingRawData ? "_data" : "", DateUtils.dayOfWeek(new Date()) == Calendar.FRIDAY ? "" : "_test");
    }

    public static String getClassUrl() {
        return String.format("%s%s", baseUrl, getClassPath());
    }

    private static String getBatchUrl() {
        return String.format("%s%s", baseUrl, "/parse/batch");
    }

    private static String post(String name, String jsonStr) {
        if (jsonStr == null || jsonStr.trim().length() <= 0) {
            return null;
        }
        System.out.printf("Post report: %s, %s, %s\r\n", name, getClassUrl(), jsonStr.length() > 100 ? String.format("%s...", jsonStr.substring(0, 100)) : jsonStr);
        return HttpClientUtil.sendHttpPostJson(getClassUrl(), headers, jsonStr.trim());
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
                put("path", String.format("%s/%s", getClassPath(), itemId));
            }};
            operationList.add(operation);
        }

        String jsonStr = JsonUtil.toString(new HashMap<String, Object>() {{
            put("requests", operationList);
        }});
        return HttpClientUtil.sendHttpPostJson(getBatchUrl(), headers, jsonStr);
    }

    public static JSONArray get() {
        String jsonStr = HttpClientUtil.sendHttpGet(getClassUrl(), headers);
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
