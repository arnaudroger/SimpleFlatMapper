package org.sfm.jdbc.impl;

import org.sfm.jdbc.impl.setter.PreparedStatementIndexSetter;
import org.sfm.reflect.Getter;

import java.sql.PreparedStatement;

public class SingleMultiIndexFieldMapper<T, P> implements MultiIndexFieldMapper<T,P> {
    private final PreparedStatementIndexSetter<P> setter;
    private final Getter<T, P> getter;

    public SingleMultiIndexFieldMapper(PreparedStatementIndexSetter<P> setter, Getter<T, P> getter) {
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
