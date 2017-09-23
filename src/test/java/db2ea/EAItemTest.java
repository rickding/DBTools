package db2ea;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2017/9/23.
 */
public class EAItemTest {
    @Test
    public void testGetId() {
        Map<EAItem, String> mapIO = new HashMap<EAItem, String>() {{
            put(new EAItem(null, null, null, null), "");
            put(new EAItem(null, null, null, new EAItem(null, null, null, null)), "");
            put(new EAItem(null, null, null, new EAItem("parent", null, null, null)), "parent");

            put(new EAItem("item", null, null, null), "item");
            put(new EAItem("item", null, null, new EAItem(null, null, null, null)), "item");
            put(new EAItem("item", null, null, new EAItem("parent", null, null, null)), "parent_item");
        }};

        for (Map.Entry<EAItem, String> io : mapIO.entrySet()) {
            String ret = io.getKey().getId();
            Assert.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testToString() {
        Map<EAItem, String> mapIO = new HashMap<EAItem, String>() {{
            put(new EAItem(null, null, null, null), ",,,,");
            put(new EAItem(null, null, null, new EAItem(null, null, null, null)), ",,,,");
            put(new EAItem(null, null, null, new EAItem("parent", null, null, null)), ",,,parent,parent");

            put(new EAItem("item", null, null, null), "item,,,item,");
            put(new EAItem("item", EAType.Class, null, new EAItem(null, null, null, null)), String.format("item,%s,,item,", EAType.Class.getCode()));
            put(new EAItem("item", null, EAStereotype.Field, new EAItem("parent", null, null, null)), String.format("item,,%s,parent_item,parent", EAStereotype.Field.getCode()));
        }};

        for (Map.Entry<EAItem, String> io : mapIO.entrySet()) {
            String ret = io.getKey().toString();
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
