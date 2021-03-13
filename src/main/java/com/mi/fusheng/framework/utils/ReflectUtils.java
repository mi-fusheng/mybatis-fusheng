package com.mi.fusheng.framework.utils;

public class ReflectUtils {
	public static Class<?> resolveType(String parameterType) {
		try {
			Class<?> clazz = Class.forName(parameterType);
			return clazz;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}
