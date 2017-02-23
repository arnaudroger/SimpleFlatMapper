package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.Key;

import java.util.Arrays;
import java.util.Objects;

public class SingleValueKey extends Key {

    private final Object values;

    public SingleValueKey(Object values) {
        this.values = values;
        //IFJAVA8_START
        if (true)
            return;
        //IFJAVA8_END
        throw new IllegalArgumentException("Supported only in java8");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SingleValueKey keys = (SingleValueKey) o;

        if (true) {
            //IFJAVA8_START
            return Objects.deepEquals(values, keys.values);
            //IFJAVA8_END
        }
        throw new IllegalArgumentException("Supported only in java8");
    }

    @Override
    /**
     * copy from Array.deepHashCode0
     */
    public int hashCode() {
        int valueHash = 0;
        if (values instanceof Object[])
            valueHash = Arrays.deepHashCode((Object[]) values);
        else if (values instanceof byte[])
            valueHash = Arrays.hashCode((byte[]) values);
        else if (values instanceof short[])
            valueHash = Arrays.hashCode((short[]) values);
        else if (values instanceof int[])
            valueHash = Arrays.hashCode((int[]) values);
        else if (values instanceof long[])
            valueHash = Arrays.hashCode((long[]) values);
        else if (values instanceof char[])
            valueHash = Arrays.hashCode((char[]) values);
        else if (values instanceof float[])
            valueHash = Arrays.hashCode((float[]) values);
        else if (values instanceof double[])
            valueHash = Arrays.hashCode((double[]) values);
        else if (values instanceof boolean[])
            valueHash = Arrays.hashCode((boolean[]) values);
        else if (values != null)
            valueHash = values.hashCode();
        return valueHash;
    }
}
