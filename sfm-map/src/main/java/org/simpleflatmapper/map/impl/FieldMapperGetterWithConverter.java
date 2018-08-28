package org.simpleflatmapper.map.impl;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.fieldmapper.FieldMapperGetter;

public class FieldMapperGetterWithConverter<T, I, P> implements FieldMapperGetter<T, P> {

    private final Converter<? super I, ? extends P> converter;
    private final FieldMapperGetter<? super T, ? extends I> getter;

    public FieldMapperGetterWithConverter(Converter<? super I, ? extends P > converter, FieldMapperGetter<? super T, ? extends I> getter) {
        if (converter == null) throw new NullPointerException("converter");
        if (getter == null) throw new NullPointerException("getter");
        this.converter = converter;
        this.getter = getter;
    }

    @Override
    public P get(T target, MappingContext<?> context) throws Exception {
        I in = getter.get(target, context);
        return converter.convert(in, context);
    }
}
