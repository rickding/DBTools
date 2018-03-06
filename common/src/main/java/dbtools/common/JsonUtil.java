package dbtools.common;

import com.alibaba.fastjson.JSONObject;

public class JsonUtil {
    public static String toJson(Object javaObj) {
        if (javaObj == null) {
            return null;
        }
        return JSONObject.toJSONString(javaObj);
    }

    public static Object parseJson(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().length() <= 0) {
            return null;
        }
        return JSONObject.parse(jsonStr.trim());
    }
}
