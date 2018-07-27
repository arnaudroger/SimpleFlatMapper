package org.simpleflatmapper.util;


public interface Enumerable<T> {
    boolean next();
    T currentValue();
}
