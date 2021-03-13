package com.mi.fusheng.framework.io;

import java.io.InputStream;

public class Resources {

    public static InputStream getResourceAsStream(String location) {
        return Resources.class.getClassLoader().getResourceAsStream(location);
    }

    public static InputStream getResourceAsReader(String location) {
        return null;
    }
}
