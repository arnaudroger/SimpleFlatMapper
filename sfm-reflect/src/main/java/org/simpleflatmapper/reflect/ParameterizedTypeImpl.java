package org.simpleflatmapper.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

public class ParameterizedTypeImpl implements ParameterizedType {

    private final Class<?> rawType;
    private final Type[] types;

    public ParameterizedTypeImpl(Class<?> rawType, Type... types) {
        this.rawType = rawType;
        this.types = types;
    }

    @Override
    public Type getRawType() {
        return rawType;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return types;
    }

    @Override
    public String toString() {
        return "ParameterizedTypeImpl{" +
                "rawType=" + rawType +
                ", types=" + Arrays.toString(types) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParameterizedType)) return false;

        ParameterizedType that = (ParameterizedType) o;

        if (!rawType.equals(that.getRawType())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(types, that.getActualTypeArguments()) && that.getOwnerType() == null;

    }

    @Override
    public int hashCode() {
        int result = rawType.hashCode();
        result = 31 * result + Arrays.hashCode(types);
        return result;
    }
}
