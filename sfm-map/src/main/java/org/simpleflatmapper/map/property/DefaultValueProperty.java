package org.simpleflatmapper.map.property;


import org.simpleflatmapper.util.ConstantSupplier;
import org.simpleflatmapper.util.Supplier;

public class DefaultValueProperty<T>  {
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
