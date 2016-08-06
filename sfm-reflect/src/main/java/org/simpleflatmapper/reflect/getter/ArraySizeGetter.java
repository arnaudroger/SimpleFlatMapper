package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.primitive.IntGetter;

import java.lang.reflect.Array;

public class ArraySizeGetter implements IntGetter<Object> {
    @Override
    public int getInt(Object target) throws Exception {
        return Array.getLength(target);
    }
}
