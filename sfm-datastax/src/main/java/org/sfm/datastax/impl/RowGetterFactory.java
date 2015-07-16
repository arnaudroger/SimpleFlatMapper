package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import com.datastax.driver.core.ResultSet;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.map.ColumnDefinition;
import org.sfm.map.GetterFactory;
import org.sfm.map.impl.getter.EnumUnspeficiedTypeGetter;
import org.sfm.map.impl.getter.OrdinalEnumGetter;
import org.sfm.map.impl.getter.StringEnumGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

public class RowGetterFactory implements GetterFactory<GettableData, DatastaxColumnKey> {

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
        if (TypeHelper.isClass(target, String.class)) {
            return (Getter<GettableData, P>) new DatastaxStringGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, Date.class)) {
            return (Getter<GettableData, P>) new DatastaxDateGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, Long.class) || TypeHelper.isClass(target, long.class)) {
            return (Getter<GettableData, P>) new DatastaxLongGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, Integer.class) || TypeHelper.isClass(target, int.class)) {
            return (Getter<GettableData, P>) new DatastaxIntegerGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, Float.class) || TypeHelper.isClass(target, float.class)) {
            return (Getter<GettableData, P>) new DatastaxFloatGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, Double.class) || TypeHelper.isClass(target, double.class)) {
            return (Getter<GettableData, P>) new DatastaxDoubleGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, boolean.class) || TypeHelper.isClass(target, Boolean.class)) {
            return (Getter<GettableData, P>) new DatastaxBooleanGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, BigDecimal.class)) {
            return (Getter<GettableData, P>) new DatastaxBigDecimalGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, BigInteger.class)) {
            return (Getter<GettableData, P>) new DatastaxBigIntegerGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, UUID.class)) {
            return (Getter<GettableData, P>) new DatastaxUUIDGetter(key.getIndex());
        }
        if (TypeHelper.isClass(target, InetAddress.class)) {
            return (Getter<GettableData, P>) new DatastaxInetAddressGetter(key.getIndex());
        }
        if (TypeHelper.isEnum(target)) {
            final Getter<GettableData, ? extends Enum<?>> getter = enumGetter(key, TypeHelper.toClass(target));
            if (getter != null) {
                return (Getter<GettableData, P>)getter;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> Getter<GettableData, E> enumGetter(DatastaxColumnKey key, Class<?> enumClass) {

        if (key.getDateType() != null) {
            final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
            if (Number.class.isAssignableFrom(javaClass)) {
                return new OrdinalEnumGetter<GettableData, E>(new DatastaxIntegerGetter(key.getIndex()), (Class<E>)enumClass);
            } else if (String.class.equals(javaClass)) {
                return new StringEnumGetter<GettableData, E>(new DatastaxStringGetter(key.getIndex()), (Class<E>)enumClass);
            }
        } else {
            return new EnumUnspeficiedTypeGetter<GettableData, E>(new DatastaxObjectGetter(key.getIndex()), (Class<E>)enumClass);
        }
        return null;
    }
}
