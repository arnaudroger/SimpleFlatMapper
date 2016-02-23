package org.sfm.jdbc.impl;

import org.sfm.jdbc.MultiIndexFieldMapper;
import org.sfm.jdbc.impl.setter.PreparedStatementIndexSetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.IndexedSetter;

import java.sql.PreparedStatement;

public class SingleIndexFieldMapper<T, P> implements MultiIndexFieldMapper<T> {
    private final IndexedSetter<PreparedStatement, P> setter;
    private final Getter<T, P> getter;

    public SingleIndexFieldMapper(IndexedSetter<PreparedStatement, P> setter, Getter<T, P> getter) {
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
