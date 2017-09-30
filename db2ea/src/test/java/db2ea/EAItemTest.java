package db2ea;

import db2ea.enums.EAStereotypeEnum;
import db2ea.enums.EATypeEnum;
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
            put(new EAItem(null, null, null, new EAItem("parent", null, null, null)), "0_parent");

            put(new EAItem("item", null, null, null), "0_item");
            put(new EAItem("item", null, null, new EAItem(null, null, null, null)), "0_item");
            put(new EAItem("item", null, null, new EAItem("parent", null, null, null)), "0_parent_0_item");
        }};

        for (Map.Entry<EAItem, String> io : mapIO.entrySet()) {
            String ret = io.getKey().getId();
            Assert.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testToString() {
        Map<EAItem, String> mapIO = new HashMap<EAItem, String>() {{
            put(new EAItem(null, null, null, null), ",,,,,,");
            put(new EAItem(null, null, null, new EAItem(null, null, null, null)), ",,,,,,");
            put(new EAItem(null, null, null, new EAItem("parent", null, null, null)), ",,,,,0_parent,0_parent");

            put(new EAItem("item", null, null, null), ",\"item\",,,,0_item,");
            put(new EAItem("item", EATypeEnum.Class, null, new EAItem(null, null, null, null)), String.format(",\"item\",%s,,,0_item,", EATypeEnum.Class.getCode()));
            put(new EAItem("item", null, EAStereotypeEnum.Field, new EAItem("parent", null, null, null)), String.format(",\"item\",,%s,,0_parent_3_item,0_parent", EAStereotypeEnum.Field.getCode()));
        }};

        for (Map.Entry<EAItem, String> io : mapIO.entrySet()) {
            String ret = io.getKey().toString();
            Assert.assertEquals(io.getValue(), ret);
        }
    }
}
