package com.mi.fusheng.framework.builder;

import com.mi.fusheng.framework.config.Configuration;
import com.mi.fusheng.framework.utils.DocumentUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.dom4j.Document;
import org.dom4j.Element;

import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class XMLConfigBuilder {

    private Configuration configuration;

    public XMLConfigBuilder() {
        this.configuration = new Configuration();
    }

    public Configuration parseConfiguration(Element rootElement) {
        Element environments = rootElement.element("environments");
        parseEnvironments(environments);

        Element mappers = rootElement.element("mappers");
        parseMappers(mappers);

        return configuration;
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
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(source);
        Document document = DocumentUtils.createDocument(inputStream);

        //按照映射文件的语义进行解析
        XMLMapperBuilder mapperBuilder = new XMLMapperBuilder(configuration);
        mapperBuilder.parse(document.getRootElement());

    }
}
