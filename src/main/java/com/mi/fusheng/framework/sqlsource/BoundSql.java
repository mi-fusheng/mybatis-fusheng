package com.mi.fusheng.framework.sqlsource;

import java.util.ArrayList;
import java.util.List;

/**
 * 将解析完成的SQL语句和解析处理的参数信息集合绑定到一起
 * */
public class BoundSql {

    //已经解析完成的sql语句
    private String sql;

    //解析#｛｝时产生的参数信息集合
    private List<ParameterMapping> parameterMappings=new ArrayList<>();

    public BoundSql(String sql, List<ParameterMapping> parameterMappings) {
        this.sql = sql;
        this.parameterMappings = parameterMappings;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public List<ParameterMapping> getParameterMappings() {
        return parameterMappings;
    }

    public void addParameterMappings(ParameterMapping parameterMapping) {
        this.parameterMappings.add(parameterMapping);
    }
}
