package org.sfm.reflect.impl;


import org.sfm.reflect.Getter;

public class NullGetter<T, P> implements Getter<T, P> {
    @Override
    public String toString() {
        return "NullGetter{}";
    }

    @Override
    public P get(T target) throws Exception {
        return null;
    }
}
