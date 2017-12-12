package com.rms.db;

import com.rms.db.mapper.UserMapper;
import com.rms.db.model.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("dbService")
public class DBService {
    @Resource
    private UserMapper userMapper;

    public List<User> getUserList() {
        return userMapper.selectAll();
    }
}
