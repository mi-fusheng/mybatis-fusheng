package com.mi.fusheng.framework.factory;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.sqlsession.DefaultSqlSession;
import com.mi.fusheng.framework.sqlsession.SqlSession;

public class DefaultSqlSessionFactory implements SqlSessionFactory {
    //依赖注入
    private Configuration configuration;

    public DefaultSqlSessionFactory(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public SqlSession openSession() {
        return new DefaultSqlSession(configuration);
    }
}
