package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.MultiIndexFieldMapper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.IndexedSetter;

import java.sql.PreparedStatement;

public class SingleIndexFieldMapper<T, P> implements MultiIndexFieldMapper<T> {
    private final IndexedSetter<? super PreparedStatement, ? super P> setter;
    private final Getter<? super T, ? extends P> getter;

    public SingleIndexFieldMapper(IndexedSetter<? super PreparedStatement, ? super P> setter, Getter<? super T, ? extends P> getter) {
        this.setter = setter;
        this.getter = getter;
    }

    @Override
    public int map(PreparedStatement ps, T value, int columnIndex) throws Exception {
        setter.set(ps, getter.get(value), columnIndex + 1);
        return 1;
    }

    @Override
    public int getSize(T value) {
        return 1;
    }
}
