package com.mi.fusheng.framework.sqlsource;

public interface SqlSource {

    /**
     * 返回BoundSql而不是sql语句，注意是针对#{}的解析过程中，需要存储解析过程产生的参数名称
     * */
    BoundSql getBoundSql(Object paramObject);
}
