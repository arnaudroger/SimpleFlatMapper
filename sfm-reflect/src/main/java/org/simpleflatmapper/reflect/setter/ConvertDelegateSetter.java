package org.simpleflatmapper.reflect.setter;

import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.converter.ContextualConverter;

public class ConvertDelegateSetter<T, I, O> implements Setter<T, I> {
    private final Setter<T, O> setter;
    private final ContextualConverter<I, O> converter;

    public ConvertDelegateSetter(Setter<T, O> setter, ContextualConverter<I, O> converter) {
        this.setter = setter;
        this.converter = converter;
    }

    @Override
    public void set(T target, I value) throws Exception {
        setter.set(target, converter.convert(value, null));
    }
}
