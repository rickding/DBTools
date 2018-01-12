package dbtools.common;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class RedisUtilTest {
    @BeforeClass
    public static void beforeClass() {
        RedisUtil.open();
    }

    @AfterClass
    public static void afterClass() {
        RedisUtil.close();
    }

    @Test
    public void testSetGet() {
        if (!RedisUtil.isOpen()) {
            return;
        }

        String key = "redis_test_key", value = "value";
        RedisUtil.set(key, value);
        Assert.assertTrue(RedisUtil.exists(key));

        Assert.assertEquals(value, RedisUtil.get(key));

        RedisUtil.del(key);
        Assert.assertFalse(RedisUtil.exists(key));
    }
}
