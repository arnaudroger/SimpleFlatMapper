package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.map.setter.ContextualIndexedSetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.PreparedStatement;

public class SingleIndexFieldMapper<T, P> implements MultiIndexFieldMapper<T> {
    private final ContextualIndexedSetter<? super PreparedStatement, ? super P> setter;
    private final Getter<? super T, ? extends P> getter;

    public SingleIndexFieldMapper(ContextualIndexedSetter<? super PreparedStatement, ? super P> setter, Getter<? super T, ? extends P> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    public int map(PreparedStatement ps, T value, int columnIndex, Context context) throws Exception {
        setter.set(ps, getter.get(value), columnIndex + 1, context);
        return 1;
    }

    @Override
    public int getSize(T value) {
        return 1;
    }
}
