package org.sfm.utils;


public interface Enumarable<T> {
    boolean next();
    T currentValue();
}
