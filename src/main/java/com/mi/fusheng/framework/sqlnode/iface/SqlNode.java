package com.mi.fusheng.framework.sqlnode.iface;

import com.mi.fusheng.framework.sqlnode.DynamicContext;

/**
 * 接口是提供功能的
 * */

public interface SqlNode {

    void apply(DynamicContext context);
}
