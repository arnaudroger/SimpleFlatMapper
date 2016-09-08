package org.simpleflatmapper.map.property;

import java.lang.reflect.Type;

public class ConstantValueProperty<T>  {
    private final T value;
    private final Type type;

    public ConstantValueProperty(T value, Type type) {
        this.value = value;
        this.type = type;
    }

    public  T getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ConstantValue{" + value +
                '}';
    }
}
