package com.rms.db;

import com.alibaba.fastjson.JSON;
import com.rms.db.model.Element;
import com.rms.db.model.ElementEx;
import com.rms.db.model.User;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:rms_db/spring-mybatis.xml"})
public class DBServiceTest {
    private static Logger logger = Logger.getLogger(DBServiceTest.class);

    @Resource
    private DBService dbService;

    @Test
    public void testGetUserList() {
        List<User> ret = dbService.getUserList();
        Assert.assertNotNull(ret);
        logger.info(JSON.toJSONString(ret));
    }

    @Test
    public void testGetElementList() {
        List<ElementEx> ret = dbService.getElementList();
        Assert.assertNotNull(ret);
        logger.info(JSON.toJSONString(ret));
    }
}
