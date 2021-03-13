package com.mi.fusheng.framework.executor;

import com.mi.fusheng.framework.config.Configuration;

import java.util.List;

/**
 * 用来处理二级缓存
 */
public class CachingExecutor implements Executor {

    private Executor delegate;


    public CachingExecutor(Executor delegate) {
        this.delegate = delegate;
    }

    @Override
    public <T> List<T> query(String statementId, Configuration configuration, Object param) {
        //TODO 如果开启了二级缓存，那么从二级缓存中获取数据

        //如果没有开启二级缓存，则走一级缓存（不管是批处理的，简单的，重用的）
        return null;
    }
}
