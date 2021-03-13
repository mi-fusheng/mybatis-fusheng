package com.mi.fusheng.framework.sqlnode.iface;

import com.mi.fusheng.framework.sqlnode.DynamicContext;

import java.util.ArrayList;
import java.util.List;

/*
* 作用：存储SqlNode集合信息，并且提供对SqlNode集合的操作
* */
public class MixedSqlNode implements SqlNode {

    //解析的SqlNode集合
    private List<SqlNode> sqlNodes=new ArrayList<SqlNode>();

    public MixedSqlNode(List<SqlNode> sqlNodes) {
        this.sqlNodes = sqlNodes;
    }

    @Override
    public void apply(DynamicContext context) {
        for (SqlNode sqlNode : sqlNodes) {
            sqlNode.apply(context);
        }
    }
}
