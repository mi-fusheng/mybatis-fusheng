package com.mi.fusheng.framework.sqlnode;

import com.mi.fusheng.framework.sqlnode.iface.MixedSqlNode;
import com.mi.fusheng.framework.sqlnode.iface.SqlNode;
import com.mi.fusheng.framework.utils.OgnlUtils;

/**
 * 封装if动态标签对应的SQL信息
 */
public class IfSqlNode implements SqlNode {

    //符合OGNL语法的表达式
    private String test;

    private MixedSqlNode mixedSqlNode;

    public IfSqlNode(String test, MixedSqlNode mixedSqlNode) {
        this.test = test;
        this.mixedSqlNode = mixedSqlNode;
    }

    @Override
    public void apply(DynamicContext context) {
        //需要使用到Ognl工具类
        boolean evaluateBoolean = OgnlUtils.evaluateBoolean(test, context.getBindings().get("_parameter"));

        if (evaluateBoolean){
            mixedSqlNode.apply(context);
        }
    }
}
