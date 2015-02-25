package org.sfm.reflect.impl;


import org.sfm.reflect.Setter;

public class NullSetter<T, P> implements Setter<T, P> {
    @Override
    public void set(T target, P value) throws Exception {
    }
    @Override
    public String toString() {
        return "NullSetter{}";
    }

}
