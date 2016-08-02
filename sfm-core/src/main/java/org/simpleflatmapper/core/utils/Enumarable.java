package org.simpleflatmapper.core.utils;


public interface Enumarable<T> {
    boolean next();
    T currentValue();
}
