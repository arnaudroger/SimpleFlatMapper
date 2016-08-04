package org.simpleflatmapper.util;


public interface Enumarable<T> {
    boolean next();
    T currentValue();
}
