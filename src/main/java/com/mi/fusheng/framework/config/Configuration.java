package com.mi.fusheng.framework.config;

import com.mi.fusheng.framework.executor.CachingExecutor;
import com.mi.fusheng.framework.executor.Executor;
import com.mi.fusheng.framework.executor.SimpleExecutor;
import com.mi.fusheng.framework.handler.ParameterHandler;
import com.mi.fusheng.framework.handler.PreparedStatementHandler;
import com.mi.fusheng.framework.handler.ResultSetHandler;
import com.mi.fusheng.framework.handler.StatementHandler;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class Configuration {

    private DataSource dataSource;

    private Map<String, MappedStatement> mappedStatements = new HashMap<>();

    //是否使用缓存
    private boolean useCache = false;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public MappedStatement getMappedStatementById(String statementId) {
        return mappedStatements.get(statementId);
    }

    public void addMappedStatement(String statementId, MappedStatement mappedStatementMap) {
        mappedStatements.put(statementId, mappedStatementMap);
    }

    public Executor newExecutor() {
        //根据类型判断是批处理的执行方式、还是简单的执行方式(SimpleExecutor)、可重用的执行方式

        Executor executor = null;

        //如果Executor的类型是simple，那么会创建SimpleExecutor
        executor = new SimpleExecutor();

        //如果Executor的类型为batch，那么创建BatchExecutor


        if (useCache) {
            executor = new CachingExecutor(executor);
        }
        return executor;
    }

    public StatementHandler newStatementHandler(String statementType) {
        if ("prepared".equals(statementType)) {
            return new PreparedStatementHandler(this);
        }

        return null;
    }

    public ParameterHandler newParameterHandler() {
        return null;
    }

    public ResultSetHandler newResultSetHandler() {
        return null;
    }
}
