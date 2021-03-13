package com.mi.fusheng.framework.sqlsession;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.executor.Executor;

import java.util.List;

public class DefaultSqlSession implements SqlSession {

    private Configuration configuration;

    public DefaultSqlSession(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public <T> List<T> selectList(String statementId, Object param) {
        Executor executor = configuration.newExecutor();

        //是否使用二级缓存 CachingExecutor

        //是否使用一级缓存 BaseExecutor

        //判断是批处理的执行方式、还是简单的执行方式(SimpleExecutor)、可重用的执行方式
        return executor.query(statementId, configuration, param);
    }

    @Override
    public <T> T selectOne(String statementId, Object param) {

        return null;
    }
}
