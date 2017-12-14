package com.rms.db;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class DB {
    private static DB db = null;

    public static DB getDb() {
        synchronized ("get db") {
            if (db == null) {
                db = new DB();
            }
        }
        return db;
    }

    private SqlSession session = null;

    private DB() {
        try {
            String resource = "mybatis-config.xml";
            InputStream inputStream = Resources.getResourceAsStream(resource);
            SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
            session = sqlSessionFactory.openSession();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void commit() {
        if (session != null) {
            session.commit();
        }
    }

    public <T> T getMapper(Class<T> cls) {
        if (session == null) {
            return null;
        }
        return session.getMapper(cls);
    }
}
