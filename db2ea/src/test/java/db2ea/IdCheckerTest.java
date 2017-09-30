package db2ea;

import org.junit.Assert;
import org.junit.Test;

public class IdCheckerTest {
    @Test
    public void testIsDuplicated() {
        IdChecker checker = new IdChecker();
        Assert.assertEquals(false, checker.isDuplicated("id1"));
        Assert.assertEquals(true, checker.isDuplicated("id1"));

        Assert.assertEquals(false, checker.isDuplicated("id2"));
        Assert.assertEquals(true, checker.isDuplicated("id2"));

        Assert.assertEquals(true, checker.isDuplicated("id1"));
    }
}
