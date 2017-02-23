package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.Key;

import java.util.Arrays;

public class MultiValueKey extends Key {

    private final Object[] values;

    public MultiValueKey(Object[] values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MultiValueKey keys = (MultiValueKey) o;
        return Arrays.deepEquals(values, keys.values);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(values);
    }
}
