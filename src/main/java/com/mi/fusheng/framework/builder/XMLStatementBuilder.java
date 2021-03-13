package com.mi.fusheng.framework.builder;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.config.MappedStatement;
import com.mi.fusheng.framework.sqlsource.SqlSource;
import com.mi.fusheng.framework.utils.ReflectUtils;
import org.dom4j.Element;

/**
*
*专门用来解析select/insert/delete/update这些Statement标签的
 *
*/
public class XMLStatementBuilder {

    private String namespace;

    private Configuration configuration;

    public XMLStatementBuilder(String namespace, Configuration configuration) {
        this.namespace = namespace;
        this.configuration = configuration;
    }

    public void parseStatementElement(Element selectElement) {
        String statementId = selectElement.attributeValue("id");

        if (statementId == null || statementId.equals("")) {
            return;
        }

        // 一个CURD标签对应一个MappedStatement对象
        // 一个MappedStatement对象由一个statementId来标识，所以保证唯一性
        // statementId = namespace + "." + CRUD标签的id属性

        statementId = namespace + "." + statementId;

        String parameterType = selectElement.attributeValue("parameterType");
        Class<?> paramterClass = ReflectUtils.resolveType(parameterType);

        String resultType = selectElement.attributeValue("resultType");
        Class<?> resultClass = ReflectUtils.resolveType(resultType);

        String statementType = selectElement.attributeValue("statementType");
        statementType = statementType == null || statementType == "" ? "prepared" : statementType;

        //SqlSource的封装过程
        SqlSource sqlSource = createSqlSource(selectElement);

        //TODO 建议使用构造者模式去优化
        MappedStatement mappedStatement = new MappedStatement(statementId, sqlSource, statementType, paramterClass, resultClass);

        configuration.addMappedStatement(statementId, mappedStatement);

    }

    private SqlSource createSqlSource(Element selectElement) {

        XMLScriptBuilder scriptBuilder=new XMLScriptBuilder();

        //去解析select等CURD标签中的SQL脚本信息
        SqlSource sqlSource =scriptBuilder.parseScriptNode(selectElement);

        return sqlSource;
    }
}
