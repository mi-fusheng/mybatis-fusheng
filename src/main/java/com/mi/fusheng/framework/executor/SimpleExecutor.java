package com.mi.fusheng.framework.executor;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.config.MappedStatement;
import com.mi.fusheng.framework.handler.StatementHandler;
import com.mi.fusheng.framework.sqlsource.BoundSql;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimpleExecutor extends BaseExecutor {


    @Override
    public List<Object> queryFromDataBase(MappedStatement mappedStatement, Configuration configuration, BoundSql boundSql, Object param) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        List<Object> results = new ArrayList<>();

        try {

            //获取数据源对象，通过数据源获取连接
            connection = getConnection(configuration.getDataSource());

            String sql = boundSql.getSql();

            StatementHandler statementHandler = configuration.newStatementHandler(mappedStatement.getStatementType());

            statement = statementHandler.prepare(connection, sql);

            statementHandler.parameterize(statement, boundSql, param);

            resultSet = statementHandler.execute(statement,sql);

            results = statementHandler.handleResultSet(mappedStatement, resultSet);

        } catch (Exception e) {

        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }

        return results;
    }

    private Connection getConnection(DataSource dataSource) {
        try {
            Connection connection = dataSource.getConnection();
            return connection;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }
}
