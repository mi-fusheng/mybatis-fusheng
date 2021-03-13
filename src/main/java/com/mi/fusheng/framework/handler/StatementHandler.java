package com.mi.fusheng.framework.handler;

import com.mi.fusheng.framework.config.MappedStatement;
import com.mi.fusheng.framework.sqlsource.BoundSql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public interface StatementHandler {

    Statement prepare(Connection connection, String sql);

    void parameterize(Statement statement, BoundSql boundSql, Object param);

    List<Object> handleResultSet(MappedStatement mappedStatement, ResultSet resultSet);

    ResultSet execute(Statement statement, String sql);
}
