package com.mi.fusheng.framework.sqlnode;

import com.mi.fusheng.framework.sqlnode.iface.SqlNode;

public class StaticTextSqlNode implements SqlNode {
    private String sqlText;

    public StaticTextSqlNode(String sqlText) {
        this.sqlText = sqlText;
    }

    @Override
    public void apply(DynamicContext context) {
        context.appendSql(sqlText);
    }
}
