package com.mi.fusheng.framework.builder;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.config.MappedStatement;
import com.mi.fusheng.framework.sqlsource.SqlSource;
import org.dom4j.Element;

import java.util.List;

/**
 * 专门用来解析映射配置文件
 */
public class XMLMapperBuilder {

    private Configuration configuration;

    public XMLMapperBuilder(Configuration configuration) {
        this.configuration = configuration;
    }


    public void parse(Element rootElement) {
        //为了方便管理statement，statement 唯一标识
        String namespace = rootElement.attributeValue("namespace");

        List<Element> selectElements = rootElement.elements();
        for (Element selectElement : selectElements) {
            XMLStatementBuilder statementBuilder = new XMLStatementBuilder(namespace,configuration);
            statementBuilder.parseStatementElement(selectElement);
        }
    }
}
