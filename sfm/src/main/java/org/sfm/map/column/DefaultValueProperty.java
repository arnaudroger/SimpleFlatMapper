package org.sfm.map.column;


import org.sfm.utils.ConstantSupplier;
import org.sfm.utils.Supplier;

public class DefaultValueProperty<T> implements ColumnProperty {
    private final Supplier<T> value;

    public DefaultValueProperty(T value) {
        this(new ConstantSupplier<T>(value));
    }

    public DefaultValueProperty(Supplier<T> supplier) {
        this.value = supplier;
    }

    public T getValue() {
        return value.get();
    }
}
