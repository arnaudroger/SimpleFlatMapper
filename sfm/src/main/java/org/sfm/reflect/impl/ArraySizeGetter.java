package org.sfm.reflect.impl;

import org.sfm.reflect.primitive.IntGetter;

import java.lang.reflect.Array;

public class ArraySizeGetter implements IntGetter<Object> {
    @Override
    public int getInt(Object target) throws Exception {
        return Array.getLength(target);
    }
}
