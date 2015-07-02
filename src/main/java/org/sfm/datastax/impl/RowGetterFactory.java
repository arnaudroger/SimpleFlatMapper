package org.sfm.datastax.impl;

import com.datastax.driver.core.Row;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.map.ColumnDefinition;
import org.sfm.map.GetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;

public class RowGetterFactory implements GetterFactory<Row, DatastaxColumnKey> {

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<Row, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
        if (TypeHelper.isClass(target, Long.class) || TypeHelper.isClass(target, long.class)) {
            return (Getter<Row, P>) new LongRowGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, Integer.class) || TypeHelper.isClass(target, int.class)) {
            return (Getter<Row, P>) new IntegerRowGetter(key.getIndex());
        }
        return null;
    }
}
