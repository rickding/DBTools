package dbtools.common;

import com.alibaba.fastjson.JSONObject;

public class JsonUtil {
    public static String toString(Object javaObj) {
        if (javaObj == null) {
            return null;
        }
        return JSONObject.toJSONString(javaObj);
    }

    public static Object toObject(String jsonStr) {
        if (jsonStr == null || jsonStr.trim().length() <= 0) {
            return null;
        }
        return JSONObject.parse(jsonStr.trim());
    }
}
