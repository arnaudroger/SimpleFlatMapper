package org.simpleflatmapper.util;

import java.lang.reflect.Type;

public class TupleHelper {

    private TupleHelper() {}

    public static boolean isTuple(Type type) {
        return isSfmTuple(type) || isJoolTuple(type) || isKotlinTuple(type);
    }

    private static boolean isKotlinTuple(Type type) {
        String className = TypeHelper.toClass(type).getName();
        return className.equals("kotlin.Pair") || className.equals("kotlin.Triple");
    }

    public static boolean isSfmTuple(Type type) {
        String className = TypeHelper.toClass(type).getName();
        return className.startsWith("org.simpleflatmapper.tuple.Tuple")
                && !className.endsWith("Tuples");
    }

    public static boolean isJoolTuple(Type type) {
        Class<?> clazz = TypeHelper.toClass(type);
        while(clazz != null) {
            for(Class<?> i : clazz.getInterfaces()) {
                if ("org.jooq.lambda.tuple.Tuple".equals(i.getName())) {
                    return true;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return false;
    }

}
