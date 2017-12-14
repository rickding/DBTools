package com.rms.db;

import com.rms.db.model.Element;
import com.rms.db.model.User;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DBUtilTest {
    @Test
    public void testGetElementList() {
        List<Element> ret = DBUtil.getElementList();
        System.out.printf("Elements from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }

    @Test
    public void testGetUserList() {
        List<User> ret = DBUtil.getUserList();
        System.out.printf("Users from DBUtil: %d\r\n", ret == null ? 0 : ret.size());
        Assert.assertNotNull(ret);
    }
}
