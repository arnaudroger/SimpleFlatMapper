package org.sfm.datastax.impl;

import com.datastax.driver.core.GettableData;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.GetterFactory;
import org.sfm.map.getter.EnumUnspecifiedTypeGetter;
import org.sfm.map.getter.OrdinalEnumGetter;
import org.sfm.map.getter.StringEnumGetter;
import org.sfm.map.getter.joda.JodaTimeGetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
//IFJAVA8_START
import org.sfm.map.getter.time.JavaTimeGetterFactory;
import org.sfm.tuples.Tuple2;

import javax.lang.model.element.TypeElement;
import java.time.*;
//IFJAVA8_END
import java.util.*;

public class RowGetterFactory implements GetterFactory<GettableData, DatastaxColumnKey> {

    private final HashMap<Class<?>, GetterFactory<GettableData, DatastaxColumnKey>> getterFactories = new HashMap<Class<?>, GetterFactory<GettableData, DatastaxColumnKey>>();
    private final GetterFactory<GettableData, DatastaxColumnKey> dateGetterFactory = new GetterFactory<GettableData, DatastaxColumnKey>() {
        @SuppressWarnings("unchecked")
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


        getterFactories.put(Short.class, new GetterFactory<GettableData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass != null && Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableData, P>) new DatastaxGenericShortGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(short.class, getterFactories.get(Short.class));

        getterFactories.put(Integer.class, new GetterFactory<GettableData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Integer.class)) {
                    return (Getter<GettableData, P>) new DatastaxIntegerGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableData, P>) new DatastaxGenericIntegerGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(int.class, getterFactories.get(Integer.class));

        getterFactories.put(Long.class, new GetterFactory<GettableData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Long.class)) {
                    return (Getter<GettableData, P>) new DatastaxLongGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableData, P>) new DatastaxGenericLongGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(long.class, getterFactories.get(Long.class));

        getterFactories.put(Float.class, new GetterFactory<GettableData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Float.class)) {
                    return (Getter<GettableData, P>) new DatastaxFloatGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableData, P>) new DatastaxGenericFloatGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(float.class, getterFactories.get(Float.class));

        getterFactories.put(Double.class, new GetterFactory<GettableData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Double.class)) {
                    return (Getter<GettableData, P>) new DatastaxDoubleGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableData, P>) new DatastaxGenericDoubleGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(double.class, getterFactories.get(Double.class));

        getterFactories.put(BigInteger.class, new GetterFactory<GettableData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(BigInteger.class)) {
                    return (Getter<GettableData, P>) new DatastaxBigIntegerGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableData, P>) new DatastaxGenericBigIntegerGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });

        getterFactories.put(BigDecimal.class, new GetterFactory<GettableData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(BigDecimal.class)) {
                    return (Getter<GettableData, P>) new DatastaxBigDecimalGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableData, P>) new DatastaxGenericBigDecimalGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });

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

        if (boolean.class.equals(targetClass) || Boolean.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxBooleanGetter(key.getIndex());
        }

        if (UUID.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxUUIDGetter(key.getIndex());
        }
        if (InetAddress.class.equals(targetClass)) {
            return (Getter<GettableData, P>) new DatastaxInetAddressGetter(key.getIndex());
        }

        if (Collection.class.isAssignableFrom(targetClass)) {

            Type elementType = TypeHelper.getComponentTypeOfListOrArray(target);
            Class<?> dataTypeClass = key.getDateType() != null ? key.getDateType().asJavaClass() : Object.class;

            if (Set.class.equals(dataTypeClass)) {
                if (targetClass.isAssignableFrom(dataTypeClass)) {
                    return new DatastaxSetGetter(key.getIndex(), TypeHelper.toClass(elementType));
                }
            }
            if (List.class.equals(dataTypeClass)) {
                if (targetClass.isAssignableFrom(dataTypeClass)) {
                    return new DatastaxListGetter(key.getIndex(), TypeHelper.toClass(elementType));
                }
            }
        }
        if (Map.class.equals(targetClass)) {
            Tuple2<Type, Type> keyValueTypeOfMap = TypeHelper.getKeyValueTypeOfMap(target);
            return new DatastaxMapGetter(key.getIndex(), TypeHelper.toClass(keyValueTypeOfMap.first()), TypeHelper.toClass(keyValueTypeOfMap.second()));
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
