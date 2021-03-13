package com.mi.fusheng.framework.sqlsource;

import com.mi.fusheng.framework.sqlnode.DynamicContext;
import com.mi.fusheng.framework.sqlnode.iface.SqlNode;
import com.mi.fusheng.framework.utils.GenericTokenParser;
import com.mi.fusheng.framework.utils.ParameterMappingTokenHandler;

/**
 * 封装并处理${}和动态标签
 * 封装的数据的处理时机:每次getBoundSql都会处理一次MixedSqlNode
 */
public class DynamicSqlSource implements SqlSource {

    //节点信息，需要在处理SqlSource的时候，再去处理SqlNode，所以在此保存
    private SqlNode rootSqlNode;

    public DynamicSqlSource(SqlNode rootSqlNode) {
        this.rootSqlNode = rootSqlNode;
    }

    @Override
    public BoundSql getBoundSql(Object paramObject) {
        DynamicContext context = new DynamicContext(paramObject);
        //先将所有SqlNode里面存储的sql文本合并成一条完整的SQL语句（此时合并的SQL语句，依然带有#{}）
        rootSqlNode.apply(context);
        System.out.println(context.getSql());
        //将带有#{}的SQL信息进行二次处理，获取#{}中的参数名称和参数类型，最终将解析的参数名称和类型封装到ParameterMapping对象中

        /*ParameterMappingTokenHandler tokenHandler = new ParameterMappingTokenHandler();
        GenericTokenParser tokenParser = new GenericTokenParser("#{", "}", tokenHandler);

        //返回的SQL语句已经是JDBC可以执行的SQL语句了
        String sql = tokenParser.parse(context.getSql());

        return new BoundSql(sql, tokenHandler.getParameterMappings());*/

        SqlSourceParser sqlSourceParser = new SqlSourceParser();
        SqlSource sqlSource = sqlSourceParser.parse(context.getSql());

        return sqlSource.getBoundSql(paramObject);
    }
}
