package com.mi.fusheng.framework.config;

import com.mi.fusheng.framework.sqlsource.SqlSource;

public class MappedStatement {

    private String statementId;
    private SqlSource sqlSource;

    private String statementType;

    private Class<?> parameterTypeClass;
    private Class<?> resultTypeClass;


    public MappedStatement(String statementId, SqlSource sqlSource, String statementType, Class<?> parameterTypeClass, Class<?> resultTypeClass) {
        this.statementId = statementId;
        this.sqlSource = sqlSource;
        this.statementType = statementType;
        this.parameterTypeClass = parameterTypeClass;
        this.resultTypeClass = resultTypeClass;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public SqlSource getSqlSource() {
        return sqlSource;
    }

    public void setSqlSource(SqlSource sqlSource) {
        this.sqlSource = sqlSource;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public Class<?> getParameterTypeClass() {
        return parameterTypeClass;
    }

    public void setParameterTypeClass(Class<?> parameterTypeClass) {
        this.parameterTypeClass = parameterTypeClass;
    }

    public Class<?> getResultTypeClass() {
        return resultTypeClass;
    }

    public void setResultTypeClass(Class<?> resultTypeClass) {
        this.resultTypeClass = resultTypeClass;
    }
}
