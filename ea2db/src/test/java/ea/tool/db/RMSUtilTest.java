package ea.tool.db;

import com.rms.db.model.ElementEx;
import org.junit.Test;

public class RMSUtilTest {
    @Test
    public void testAddElement() {
        RMSUtil.addElement(new ElementEx(){{setName("element");}});
    }
}
