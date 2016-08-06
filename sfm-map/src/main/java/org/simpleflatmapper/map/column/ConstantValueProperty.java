package org.simpleflatmapper.map.column;

import java.lang.reflect.Type;

public class ConstantValueProperty<T> implements ColumnProperty {
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
}
