package com.mi.fusheng.framework.sqlsource;

import com.mi.fusheng.framework.utils.GenericTokenParser;
import com.mi.fusheng.framework.utils.ParameterMappingTokenHandler;

/**
 * 将DynamicSqlSource和RawSqlSource中对于#{}的处理抽取到一个类去维护
 */
public class SqlSourceParser {


    public SqlSource parse(String sqlText) {
        ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", tokenHandler);
        String sql = tokenParser.parse(sqlText);

        return new StaticSqlSource(sql, tokenHandler.getParameterMappings());
    }
}
