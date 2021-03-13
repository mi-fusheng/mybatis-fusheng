package com.mi.fusheng.test;

import com.mi.fusheng.po.User;
import com.mi.fusheng.util.SimpleTypeRegistry;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MybatisV1 {

    private Properties properties = new Properties();
    private static final String DRIVER = "db.driver";

    /*
     * 加载properties配置文件
     * */
    public void loadProPerties() {
        try {
            InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("jdbc.properties");
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Object> selectList(String statementId, Object paramObject) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Object> results = new ArrayList<>();

        try {
            //加载数据库驱动
            Class.forName(properties.getProperty(DRIVER));

            //通过驱动管理类获取数据库链接connection = DriverManager
            connection = DriverManager.getConnection(properties.getProperty("db.url"),
                    properties.getProperty("db.username"), properties.getProperty("db.password"));

            // 定义sql语句 ?表示占位符
            String sql = properties.getProperty("db.sql." + statementId);

            // 获取预处理 statement
            preparedStatement = connection.prepareStatement(sql);

            // 设置参数，第一个参数为 sql 语句中参数的序号（从 1 开始），第二个参数为设置的
            String parametertype = properties.getProperty("db.sql." + statementId + ".parametertype");
            Class<?> parameterTypeClass = Class.forName(parametertype);

            //先判断入参类型（8中基本类型，String，各种集合类型，自定义的java类型）
            if (SimpleTypeRegistry.isSimpleType(parameterTypeClass)) {
                preparedStatement.setObject(1, paramObject);
            } else {
                String params = properties.getProperty("db.sql." + statementId + ".params");
                String[] paramsArray = params.split(",");

                for (int i = 0; i < paramsArray.length; i++) {
                    //列名，如：username,sex
                    String param = paramsArray[i];

                    //反射 根据列名获取入参对象的属性值，前提：列名和属性名称要一致
                    Field field = parameterTypeClass.getDeclaredField(param);
                    field.setAccessible(true);
                    Object value = field.get(paramObject);
                    preparedStatement.setObject(i + 1, value);

                }
            }

            // 向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();

            // 获取要映射的结果类型
            String resultclassname = properties.getProperty("db.sql." + statementId + ".resultclassname");
            Class<?> resultclass = Class.forName(resultclassname);


            Object result = null;

            // 遍历查询结果集

            while (rs.next()) {
                // 每一行对应一个对象
                result = resultclass.newInstance();

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                for (int i = 0; i < columnCount; i++) {
                    // 获取结果集中列的名称
                    String columnName = metaData.getColumnName(i + 1);
                    Field field = resultclass.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(result, rs.getObject(columnName));
                }

                results.add(result);
            }

        } catch (Exception e) {

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }

            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
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

    @Test
    public void test() {
        loadProPerties();
        User user = new User();
        user.setUsername("王五");
        List<Object> list = selectList("queryUserById", user);
        System.out.println(list);
    }
}
