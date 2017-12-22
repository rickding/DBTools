package dbtools.common;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class HttpClientUtilTest {
    @Test
    public void testSendHttpPostJson() {
        String url = "http://localhost:1337/parse/classes/GameScore";
        Map<String, String> headers = new HashMap<String, String>() {{
            put("X-Parse-Application-Id", "myAppId");
            put("X-Parse-Master-Key", "myMasterKey");
        }};

        String param = "{\"score\":1337,\"playerName\":\"Sean from Java\",\"cheatMode\":false}";

        String ret = HttpClientUtil.sendHttpPostJson(url, headers, param);
        System.out.println(ret);
    }
}
