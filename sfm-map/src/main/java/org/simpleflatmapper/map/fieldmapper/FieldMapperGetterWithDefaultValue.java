package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class FieldMapperGetterWithDefaultValue<T, P> implements FieldMapperGetter<T, P> {
    private final P defaultValue;
    private final FieldMapperGetter<? super T, ? extends P> delegate;

    public FieldMapperGetterWithDefaultValue(FieldMapperGetter<? super T, ? extends P> delegate, P defaultValue) {
        this.delegate = delegate;
        this.defaultValue = defaultValue;
    }

    @Override
    public P get(T t, MappingContext<?> context) throws Exception {
        P p = delegate.get(t, context);

        if (p == null) return defaultValue;

        return p;
    }
}
