package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetter;

public class FieldMapperGetterWithConverter<T, I, P> implements ContextualGetter<T, P> {

    private final Converter<? super I, ? extends P> converter;
    private final ContextualGetter<? super T, ? extends I> getter;

    public FieldMapperGetterWithConverter(Converter<? super I, ? extends P > converter, ContextualGetter<? super T, ? extends I> getter) {
        if (converter == null) throw new NullPointerException("converter");
        if (getter == null) throw new NullPointerException("getter");
        this.converter = converter;
        this.getter = getter;
    }

    @Override
    public P get(T target, Context context) throws Exception {
        I in = getter.get(target, context);
        return converter.convert(in, context);
    }
}
