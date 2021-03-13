package com.mi.fusheng.test;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.config.MappedStatement;
import com.mi.fusheng.framework.sqlnode.IfSqlNode;
import com.mi.fusheng.framework.sqlnode.StaticTextSqlNode;
import com.mi.fusheng.framework.sqlnode.TextSqlNode;
import com.mi.fusheng.framework.sqlnode.iface.MixedSqlNode;
import com.mi.fusheng.framework.sqlnode.iface.SqlNode;
import com.mi.fusheng.framework.sqlsource.*;
import com.mi.fusheng.po.User;
import com.mi.fusheng.util.SimpleTypeRegistry;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 目的是使用XML来表达mybatis的全局配置信息，和业务相关的SQL映射信息 (映射文件) 其次，优化数据连接的创建（使用连接池）
 */
public class MybatisV2 {

    /**
     * 封装了全局配置文件和映射文件中的所有信息
     */
    private Configuration configuration = new Configuration();
    private String namespace;

    private boolean isDynamic = false;

    @Test
    public void test() {
        loadConfiguration();

        User user = new User();
        user.setId(1);
        user.setUsername("王五");
        List<User> users = selectList("test.findUserById", user);
        System.out.println(users);
    }

    private void loadConfiguration() {
        //指定全局配置文件的路径
        String location = "mybatis-config.xml";
        //获取对应的InputStream对象
        InputStream inputStream = getInputStream(location);
        //获取Document对象
        Document document = createDocument(inputStream);
        //按照全局配置文件和映射文件的XML语义仅需解析
        loadConfigurationElement(document.getRootElement());

    }

    /**
     * rootElement<configuration >
     */
    private void loadConfigurationElement(Element rootElement) {
        Element environments = rootElement.element("environments");
        parseEnvironments(environments);

        Element mappers = rootElement.element("mappers");
        parseMappers(mappers);
    }


    /**
     * @param mappers <mappers>
     */
    private void parseMappers(Element mappers) {
        List<Element> mapperElements = mappers.elements("mapper");

        for (Element mapperElement : mapperElements) {
            parseMapper(mapperElement);
        }
    }

    private void parseMapper(Element mapperElement) {
        String source = mapperElement.attributeValue("resource");
        InputStream inputStream = getInputStream(source);
        Document document = createDocument(inputStream);

        //按照映射文件的语义进行解析
        parseXmlMapper(document.getRootElement());

    }

    private void parseXmlMapper(Element rootElement) {
        //为了方便管理statement，statement 唯一标识
        namespace = rootElement.attributeValue("namespace");

        List<Element> selectElements = rootElement.elements();
        for (Element selectElement : selectElements) {
            parseStatementElement(selectElement);
        }

    }

    private void parseStatementElement(Element selectElement) {
        String statementId = selectElement.attributeValue("id");

        if (statementId == null || statementId.equals("")) {
            return;
        }

        // 一个CURD标签对应一个MappedStatement对象
        // 一个MappedStatement对象由一个statementId来标识，所以保证唯一性
        // statementId = namespace + "." + CRUD标签的id属性

        statementId = namespace + "." + statementId;

        String parameterType = selectElement.attributeValue("parameterType");
        Class<?> paramterClass = resolveType(parameterType);

        String resultType = selectElement.attributeValue("resultType");
        Class<?> resultClass = resolveType(resultType);

        String statementType = selectElement.attributeValue("statementType");
        statementType = statementType == null || statementType == "" ? "prepared" : statementType;

        //SqlSource的封装过程
        SqlSource sqlSource = createSqlSource(selectElement);

        //TODO 建议使用构造者模式去优化
        MappedStatement mappedStatement = new MappedStatement(statementId, sqlSource, statementType, paramterClass, resultClass);

        configuration.addMappedStatement(statementId, mappedStatement);

    }

    private SqlSource createSqlSource(Element selectElement) {
        //去解析select等CURD标签中的SQL脚本信息
        SqlSource sqlSource = parseScriptNode(selectElement);

        return sqlSource;
    }

    private SqlSource parseScriptNode(Element selectElement) {
        //解析select标签下的所有SQL脚本信息，比如sql文本片段，动态标签等，最终封装成SqlNode集合

        MixedSqlNode rootSqlNode = parseDynamicTags(selectElement);
        //在解析SqlNode的时候，我们需要知道这sql文本是否包含${}，或者sql脚本中是否包含动态标签


        SqlSource sqlSource = null;
        //包含${}和动态标签的这种SQL信息，封装到DynamicSqlSource
        if (isDynamic) {
            sqlSource = new DynamicSqlSource(rootSqlNode);
        } else {
            sqlSource = new RawSqlSource(rootSqlNode);
        }

        //如果整个SQL信息中只包含#{}或无特殊的字符，则将SQL信息封装到RawSqlSource

        return sqlSource;
    }

    private MixedSqlNode parseDynamicTags(Element selectElement) {
        List<SqlNode> sqlNodes = new ArrayList<>();

        //获取select标签的子节点
        int nodeCount = selectElement.nodeCount();

        //遍历所有节点
        for (int i = 0; i < nodeCount; i++) {
            Node node = selectElement.node(i);

            // 判断节点是文本节点还是Element节点
            if (node instanceof Text) {
                //获取SQL文本
                String sqlText = node.getText().trim();
                if (sqlText == null || "".equals(sqlText)) {
                    continue;
                }

                // 如果是文本节点，判断文本节点是否包含${}
                TextSqlNode textSqlNode = new TextSqlNode(sqlText);
                // 如果包含${}，那么将文本节点的SQL文本封装TextSqlNode
                if (textSqlNode.isDynamic()) {
                    sqlNodes.add(textSqlNode);
                    isDynamic = true;
                } else {
                    // 如果不包含${}，那么将文本节点的SQL文本封装到StaticTextSqlNode
                    sqlNodes.add(new StaticTextSqlNode(sqlText));
                }

            } else if (node instanceof Element) {
                //如果是Element节点，则需要获取节点名称
                Element element = (Element) node;
                String name = element.getName();
                //此处使用策略模式去优化
                //根据名称创建对应Element的SqlNode对象，封装到Element的节点数据，比如if标签IfSqlNode
                if ("if".equals(name)) {
                    String test = element.attributeValue("test");
                    //递归给if标签动态解析，获取它的子标签SqlNode节点集合
                    MixedSqlNode mixedSqlNode = parseDynamicTags(element);

                    IfSqlNode ifSqlNode = new IfSqlNode(test, mixedSqlNode);
                    sqlNodes.add(ifSqlNode);
                } else if ("where".equals(name)) {
                    //，，，，，，，，，
                }

            }
        }
        return new MixedSqlNode(sqlNodes);
    }

    private Class<?> resolveType(String parameterType) {
        try {
            Class<?> clazz = Class.forName(parameterType);
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * @param environments <environments>
     */
    private void parseEnvironments(Element environments) {
        String def = environments.attributeValue("default");
        List<Element> elements = environments.elements();

        for (Element element : elements) {
            String id = element.attributeValue("id");

            if (id.equals(def)) {
                parseDataSource(element);
            }
        }

    }

    /**
     * @param element <environment>
     */
    private void parseDataSource(Element element) {
        Element dataSourceElement = element.element("dataSource");
        String type = dataSourceElement.attributeValue("type");

        if (type.equals("DBCP")) {
            BasicDataSource dataSource = new BasicDataSource();

            Properties properties = parseProperty(dataSourceElement);

            dataSource.setDriverClassName(properties.getProperty("driver"));
            dataSource.setUrl(properties.getProperty("url"));
            dataSource.setUsername(properties.getProperty("username"));
            dataSource.setPassword(properties.getProperty("password"));

            configuration.setDataSource(dataSource);
        }


    }

    private Properties parseProperty(Element dataSourceElement) {
        Properties properties = new Properties();

        List<Element> proElements = dataSourceElement.elements("property");

        for (Element proElement : proElements) {
            String name = proElement.attributeValue("name");
            String value = proElement.attributeValue("value");

            properties.put(name, value);
        }

        return properties;
    }

    private Document createDocument(InputStream inputStream) {
        try {
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(inputStream);
            return document;
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return null;
    }

    private InputStream getInputStream(String location) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(location);
        return inputStream;
    }

    @SuppressWarnings("unchecked")
    private <T> List<T> selectList(String statementId, Object paramObject) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        List<Object> results = new ArrayList<>();

        try {

            //获取数据源对象，通过数据源获取连接
            connection = getConnection(configuration.getDataSource());

            MappedStatement mappedStatement = configuration.getMappedStatementById(statementId);
            SqlSource sqlSource = mappedStatement.getSqlSource();
            //sqlSource的执行过程
            BoundSql boundSql = sqlSource.getBoundSql(paramObject);

            String sql = boundSql.getSql();

            // 获取预处理 statement
            preparedStatement = connection.prepareStatement(sql);
            //设置参数
            System.out.println(sql);
            handleParamter(preparedStatement, mappedStatement, boundSql, paramObject);


            // 向数据库发出 sql 执行查询，查询出结果集
            rs = preparedStatement.executeQuery();

            //获取要映射的结果类型
            handleResultSet(rs, results, mappedStatement);


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

        return (List<T>) results;
    }

    private void handleResultSet(ResultSet rs, List<Object> results, MappedStatement mappedStatement) throws Exception {
        Class<?> resultclass = mappedStatement.getResultTypeClass();

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
    }

    private void handleParamter(PreparedStatement preparedStatement, MappedStatement mappedStatement, BoundSql boundSql, Object paramObject) throws Exception {

        Class<?> parameterTypeClass = mappedStatement.getParameterTypeClass();

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
        }
    }

    private String getSql(MappedStatement mappedStatement) {

        return null;
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
