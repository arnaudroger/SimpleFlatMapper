package org.simpleflatmapper.util;

public interface BiPredicate<T, V> {
    boolean test(T t, V v);
}
