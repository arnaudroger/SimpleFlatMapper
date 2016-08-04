package org.simpleflatmapper.util;


public class Asserts {
    public static <T> T requireNonNull(String name, T obj) {
        if (obj == null) {
            throw new NullPointerException(name + " is null");
        }
        return obj;
    }
}
