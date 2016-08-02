package org.simpleflatmapper.core.map.column;


import org.simpleflatmapper.core.utils.ConstantSupplier;
import org.simpleflatmapper.core.utils.Supplier;

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
