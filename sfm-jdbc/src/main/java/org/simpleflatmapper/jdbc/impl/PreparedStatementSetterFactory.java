package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.JdbcColumnKey;

import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.IndexedSetterFactory;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.jdbc.impl.setter.BooleanPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.BytePreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.CharacterPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.DoublePreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.FloatPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.IntegerPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.LongPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.PreparedStatementSetterImpl;
import org.simpleflatmapper.jdbc.impl.setter.ShortPreparedStatementSetter;

import java.lang.reflect.Type;
import java.sql.*;

public class PreparedStatementSetterFactory implements
        SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> {

    public static final PreparedStatementSetterFactory INSTANCE = new PreparedStatementSetterFactory(PreparedStatementIndexedSetterFactory.INSTANCE);

    private final IndexedSetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> preparedStatementIndexedSetterFactory;

    private PreparedStatementSetterFactory(IndexedSetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> preparedStatementIndexedSetterFactory) {
        this.preparedStatementIndexedSetterFactory = preparedStatementIndexedSetterFactory;
    }


    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
        int columnIndex = pm.getColumnKey().getIndex();

        Type type = pm.getPropertyMeta().getPropertyType();

        Class<?> clazz = TypeHelper.toBoxedClass(type);

        if (Boolean.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new BooleanPreparedStatementSetter(columnIndex);
        } else if (Byte.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new BytePreparedStatementSetter(columnIndex);
        } else if (Character.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new CharacterPreparedStatementSetter(columnIndex);
        } else if (Short.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new ShortPreparedStatementSetter(columnIndex);
        } else if (Integer.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new IntegerPreparedStatementSetter(columnIndex);
        } else if (Long.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new LongPreparedStatementSetter(columnIndex);
        } else if (Double.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new DoublePreparedStatementSetter(columnIndex);
        } else if (Float.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new FloatPreparedStatementSetter(columnIndex);
        }

        IndexedSetter<PreparedStatement, P> setter = preparedStatementIndexedSetterFactory.getIndexedSetter(pm);

        if (setter != null) {
            return new PreparedStatementSetterImpl<P>(columnIndex, setter);
        } else return null;
    }

}
