package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.context.Key;

import java.util.Arrays;

public final class MultiValueKey extends Key {

    private final Object[] values;
    private final int _hashCode;

    public MultiValueKey(Object[] values) {
        this.values = values;
        this._hashCode = Arrays.deepHashCode(values);
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
        return _hashCode;
    }
}
