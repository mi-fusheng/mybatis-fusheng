package com.mi.fusheng.framework.handler;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.config.MappedStatement;
import com.mi.fusheng.framework.sqlsource.BoundSql;
import com.mi.fusheng.framework.sqlsource.ParameterMapping;
import com.mi.fusheng.util.SimpleTypeRegistry;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PreparedStatementHandler implements StatementHandler {

    private ParameterHandler parameterHandler;

    private ResultSetHandler resultSetHandler;

    public PreparedStatementHandler(Configuration configuration) {
        this.parameterHandler = configuration.newParameterHandler();
        this.resultSetHandler = configuration.newResultSetHandler();
    }

    @Override
    public Statement prepare(Connection connection, String sql) {
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return preparedStatement;
    }

    @Override
    public void parameterize(Statement statement, BoundSql boundSql, Object paramObject) {
        try {
            PreparedStatement preparedStatement = (PreparedStatement) statement;


         /*   Class<?> parameterTypeClass = paramObject.getClass();

            //先判断入参类型（8中基本类型，String，各种集合类型，自定义的java类型）
            if (SimpleTypeRegistry.isSimpleType(parameterTypeClass)) {

                preparedStatement.setObject(1, paramObject);

            } else {
                List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();

                for (int i = 0; i < parameterMappings.size(); i++) {
                    //列名，如：username,sex
                    ParameterMapping parameterMapping = parameterMappings.get(i);

                    //反射 根据列名获取入参对象的属性值，前提：列名和属性名称要一致
                    Field field = parameterTypeClass.getDeclaredField(parameterMapping.getName());
                    field.setAccessible(true);
                    Object value = field.get(paramObject);
                    preparedStatement.setObject(i + 1, value);

                }
            }*/
        } catch (Exception throwables) {
            throwables.printStackTrace();
        }
    }

    @Override
    public List<Object> handleResultSet(MappedStatement mappedStatement, ResultSet resultSet) {
        try {
            List<Object> results = new ArrayList<>();
            Class<?> resultclass = mappedStatement.getResultTypeClass();

            Object result = null;
            // 遍历查询结果集

            while (resultSet.next()) {
                // 每一行对应一个对象
                result = resultclass.newInstance();

                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 0; i < columnCount; i++) {
                    // 获取结果集中列的名称
                    String columnName = metaData.getColumnName(i + 1);
                    Field field = null;

                    field = resultclass.getDeclaredField(columnName);

                    field.setAccessible(true);
                    field.set(result, resultSet.getObject(columnName));
                }

                results.add(result);
            }

            return results;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public ResultSet execute(Statement statement, String sql) {
        try {
            PreparedStatement preparedStatement = (PreparedStatement) statement;
            return preparedStatement.executeQuery();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }
}
