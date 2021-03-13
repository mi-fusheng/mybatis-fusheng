package com.mi.fusheng.framework.sqlnode;

import java.util.HashMap;
import java.util.Map;

/**
 * SqlNode处理过程中的动态上下文
 */
public class DynamicContext {

    private StringBuffer sb = new StringBuffer();

    private Map<String, Object> bindings = new HashMap<>();

    public DynamicContext(Object paramObject) {
        bindings.put("_parameter", paramObject);
    }

    public void appendSql(String sqlText) {
        sb.append(sqlText);
        sb.append(" ");
    }

    public String getSql() {
        return sb.toString();
    }

    public Map<String, Object> getBindings() {
        return bindings;
    }
}
