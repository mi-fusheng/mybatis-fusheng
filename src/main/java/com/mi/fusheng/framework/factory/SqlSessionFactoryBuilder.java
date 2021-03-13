package com.mi.fusheng.framework.factory;

import com.mi.fusheng.framework.builder.XMLConfigBuilder;
import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.utils.DocumentUtils;
import org.dom4j.Document;

import java.io.InputStream;
import java.io.Reader;

/**
 * 使用构建者模式去创建SqLSessionFactory
 * 因为SqlSessionFactory需要Configuration对象
 * 驾照Configuration对象的方式，可以通过字节流，也可以通过字符流
 */
public class SqlSessionFactoryBuilder {

    public SqlSessionFactory build(InputStream inputStream) {
        Document document = DocumentUtils.createDocument(inputStream);
        XMLConfigBuilder configBuilder = new XMLConfigBuilder();
        Configuration configuration = configBuilder.parseConfiguration(document.getRootElement());
        return build(configuration);
    }

    public SqlSessionFactory build(Reader reader) {
        Configuration configuration = null;
        return build(configuration);
    }

    private SqlSessionFactory build(Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }
}
