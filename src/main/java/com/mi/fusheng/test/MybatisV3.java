package com.mi.fusheng.test;

import com.mi.fusheng.framework.factory.SqlSessionFactory;
import com.mi.fusheng.framework.factory.SqlSessionFactoryBuilder;
import com.mi.fusheng.framework.io.Resources;
import com.mi.fusheng.framework.sqlsession.SqlSession;
import com.mi.fusheng.po.User;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

public class MybatisV3 {
    private SqlSessionFactory sqlSessionFactory;


    @Before
    public void build() {
        String location = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(location);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);
    }

    @Test
    public void test() {

        //调用者只关心SqlSession是如何使用，而不关心SqlSession如何创建
        SqlSession sqlSession = sqlSessionFactory.openSession();

        User user = new User();
        user.setId(1);
        user.setUsername("王五");
        List<User> users = sqlSession.selectList("test.findUserById", user);
        System.out.println(users);
    }
}
