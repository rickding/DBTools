package com.rms.db;

import com.rms.db.mapper.ElementMapper;
import com.rms.db.mapper.UserMapper;
import com.rms.db.model.Element;
import com.rms.db.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("dbService")
public class DBService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private ElementMapper elementMapper;

    public List<User> getUserList() {
        return userMapper.selectAll();
    }

    public List<Element> getElementList() {
        return elementMapper.selectAll();
    }
}
