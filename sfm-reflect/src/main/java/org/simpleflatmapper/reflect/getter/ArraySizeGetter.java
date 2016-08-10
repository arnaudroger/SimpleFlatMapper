package org.simpleflatmapper.reflect.getter;

import org.simpleflatmapper.reflect.primitive.IntGetter;

import java.lang.reflect.Array;

public class ArraySizeGetter implements IntGetter<Object> {
    @Override
    public int getInt(Object target) throws IllegalArgumentException {
        return Array.getLength(target);
    }
}
