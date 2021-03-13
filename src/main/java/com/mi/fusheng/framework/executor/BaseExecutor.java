package com.mi.fusheng.framework.executor;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.config.MappedStatement;
import com.mi.fusheng.framework.sqlsource.BoundSql;
import com.mi.fusheng.framework.sqlsource.SqlSource;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理一级缓存
 */
public abstract class BaseExecutor implements Executor {

    private Map<String, List<Object>> oneLevelCahe = new HashMap<String, List<Object>>();

    @Override
    public <T> List<T> query(String statementId, Configuration configuration, Object param) {
        MappedStatement mappedStatement = configuration.getMappedStatementById(statementId);
        SqlSource sqlSource = mappedStatement.getSqlSource();
        BoundSql boundSql = sqlSource.getBoundSql(param);

        List<Object> list = oneLevelCahe.get(boundSql);

        if (list != null && list.size() > 0) {
            return (List<T>) list;
        }

        list = queryFromDataBase(mappedStatement, configuration, boundSql, param);

        oneLevelCahe.put(boundSql.getSql(), list);

        return (List<T>) list;
    }

    public abstract List<Object> queryFromDataBase(MappedStatement mappedStatement, Configuration configuration, BoundSql boundSql, Object param);

}
