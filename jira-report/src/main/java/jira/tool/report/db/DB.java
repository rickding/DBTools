package jira.tool.report.db;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.InputStream;

public class DB {
    private static DB db = null;

    public static DB getDb() {
        if (db != null) {
            return db;
        }

        synchronized ("create db") {
            db = new DB();
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

    public <T> T getMapper(Class<T> cls) {
        if (session == null) {
            return null;
        }
        return session.getMapper(cls);
    }
}
