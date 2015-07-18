package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.csv.CsvColumnKey;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.map.ColumnDefinition;
import org.sfm.map.GetterFactory;
import org.sfm.map.impl.getter.EnumUnspecifiedTypeGetter;
import org.sfm.map.impl.getter.OrdinalEnumGetter;
import org.sfm.map.impl.getter.StringEnumGetter;
import org.sfm.map.impl.getter.joda.JodaTimeGetterFactory;
import org.sfm.map.impl.getter.time.JavaTimeGetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.time.*;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class RowGetterFactory implements GetterFactory<GettableData, DatastaxColumnKey> {

    private final HashMap<Class<?>, GetterFactory<GettableData, DatastaxColumnKey>> getterFactories = new HashMap<Class<?>, GetterFactory<GettableData, DatastaxColumnKey>>();
    private final GetterFactory<GettableData, DatastaxColumnKey> dateGetterFactory = new GetterFactory<GettableData, DatastaxColumnKey>() {
        @Override
        public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
            return (Getter<GettableData, P>) new DatastaxDateGetter(key.getIndex());
        }
    };

    private JodaTimeGetterFactory<GettableData, DatastaxColumnKey> jodaTimeGetterFactory;

    public RowGetterFactory() {
        //IFJAVA8_START
        JavaTimeGetterFactory<GettableData, DatastaxColumnKey> javaTimeGetterFactory =
                new JavaTimeGetterFactory<GettableData, DatastaxColumnKey>(dateGetterFactory);
        getterFactories.put(LocalDate.class, javaTimeGetterFactory);
        getterFactories.put(LocalDateTime.class, javaTimeGetterFactory);
        getterFactories.put(LocalTime.class, javaTimeGetterFactory);
        getterFactories.put(OffsetDateTime.class, javaTimeGetterFactory);
        getterFactories.put(OffsetTime.class, javaTimeGetterFactory);
        getterFactories.put(ZonedDateTime.class, javaTimeGetterFactory);
        getterFactories.put(Instant.class, javaTimeGetterFactory);
        getterFactories.put(Year.class, javaTimeGetterFactory);
        getterFactories.put(YearMonth.class, javaTimeGetterFactory);
        //IFJAVA8_END

        jodaTimeGetterFactory = new JodaTimeGetterFactory<GettableData, DatastaxColumnKey>(dateGetterFactory);

    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
        Class<?> targetClass = TypeHelper.toClass(target);
        if (String.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxStringGetter(key.getIndex());
        }
        if (Date.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxDateGetter(key.getIndex());
        }
        if (Long.class.equals(targetClass) || long.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxLongGetter(key.getIndex());
        }
        if (Integer.class.equals(targetClass) || int.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxIntegerGetter(key.getIndex());
        }
        if (Float.class.equals(targetClass) || float.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxFloatGetter(key.getIndex());
        }
        if (Double.class.equals(targetClass) || double.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxDoubleGetter(key.getIndex());
        }
        if (boolean.class.equals(targetClass) || Boolean.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxBooleanGetter(key.getIndex());
        }
        if (BigDecimal.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxBigDecimalGetter(key.getIndex());
        }
        if (BigInteger.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxBigIntegerGetter(key.getIndex());
        }
        if (UUID.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxUUIDGetter(key.getIndex());
        }
        if (InetAddress.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxInetAddressGetter(key.getIndex());
        }
        if (TypeHelper.isEnum(target)) {
            final Getter<GettableData, ? extends Enum> getter = enumGetter(key, TypeHelper.toClass(target));
            if (getter != null) {
                return (Getter<GettableData, P>)getter;
            }
        }

        final GetterFactory<GettableData, DatastaxColumnKey> rowGetterFactory = getterFactories.get(targetClass);

        if (rowGetterFactory != null) {
            return rowGetterFactory.newGetter(target, key, columnDefinition);
        }

        final Getter<GettableData, P> getter = jodaTimeGetterFactory.newGetter(target, key, columnDefinition);

        if (getter != null) {
            return getter;
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
            return new EnumUnspecifiedTypeGetter<GettableData, E>(new DatastaxObjectGetter(key.getIndex()), (Class<E>)enumClass);
        }
        return null;
    }
}
