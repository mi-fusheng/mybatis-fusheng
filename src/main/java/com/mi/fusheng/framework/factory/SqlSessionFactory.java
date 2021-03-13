package com.mi.fusheng.framework.factory;

import com.mi.fusheng.framework.sqlsession.SqlSession;

public interface SqlSessionFactory {

    SqlSession openSession();
}
