package ea.tool.api;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

public class EATypeEnumTest {
    @Test
    public void testIsMappedToStory() {
        Map<String, Boolean> mapIO = new HashMap<String, Boolean>(){{
           put(null, false);
           put("", false);
           put("test", false);
           put("Package", false);
           put("Requirement", true);
        }};

        for (Map.Entry<String, Boolean> io : mapIO.entrySet()) {
            Boolean ret = EATypeEnum.isMappedToStory(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testIsSavedType() {
        Map<String, Boolean> mapIO = new HashMap<String, Boolean>(){{
            put(null, false);
            put("", false);
            put("test", false);
            put("Package", true);
            put("Requirement", true);
        }};

        for (Map.Entry<String, Boolean> io : mapIO.entrySet()) {
            Boolean ret = EATypeEnum.isSavedType(io.getKey());
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
