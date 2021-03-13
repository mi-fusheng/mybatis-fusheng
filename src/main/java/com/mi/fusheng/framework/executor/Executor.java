package com.mi.fusheng.framework.executor;


import com.mi.fusheng.framework.config.Configuration;

import java.util.List;

/**
 * 执行器:将不同的执行逻辑进行抽象
 */
public interface Executor {

    <T> List<T> query(String statementId, Configuration configuration, Object param);

}
