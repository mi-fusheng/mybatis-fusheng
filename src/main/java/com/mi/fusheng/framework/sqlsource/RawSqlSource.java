package com.mi.fusheng.framework.sqlsource;

import com.mi.fusheng.framework.sqlnode.DynamicContext;
import com.mi.fusheng.framework.sqlnode.iface.SqlNode;
import com.mi.fusheng.framework.utils.GenericTokenParser;
import com.mi.fusheng.framework.utils.ParameterMappingTokenHandler;

/**
 * 封装并处理#{}的SQL信息
 * 封装的数据的处理时机:只需要被处理一次就可以了，那么在构造方法中处理#{}再合适不过了
 */
public class RawSqlSource implements SqlSource {

    private SqlSource sqlSource;

    public RawSqlSource(SqlNode rootSqlNode) {
        DynamicContext context = new DynamicContext(null);
        //先将所有SqlNode里面存储的sql文本合并成一条完整的SQL语句（此时合并的SQL语句，依然带有#{}）
        rootSqlNode.apply(context);
        //将带有#{}的SQL信息进行二次处理，获取#{}中的参数名称和参数类型，最终将解析的参数名称和类型封装到ParameterMapping对象中

        SqlSourceParser sqlSourceParser = new SqlSourceParser();
        sqlSource = sqlSourceParser.parse(context.getSql());
      /*  ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", tokenHandler);
        String sql = tokenParser.parse(context.getSql());
        sqlSource = new StaticSqlSource(sql, tokenHandler.getParameterMappings());*/
    }

    @Override
    public BoundSql getBoundSql(Object paramObject) {
        return sqlSource.getBoundSql(paramObject);
    }
}
