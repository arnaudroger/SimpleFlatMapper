package org.sfm.datastax.impl;

import com.datastax.driver.core.*;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.datastax.DatastaxMapperFactory;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.GetterFactory;
import org.sfm.map.getter.EnumUnspecifiedTypeGetter;
import org.sfm.map.getter.OrdinalEnumGetter;
import org.sfm.map.getter.StringEnumGetter;
import org.sfm.map.getter.joda.JodaTimeGetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;

import org.sfm.tuples.Tuple2;
import org.sfm.tuples.Tuples;

//IFJAVA8_START
import org.sfm.map.getter.time.JavaTimeGetterFactory;
import java.time.*;
//IFJAVA8_END
import java.util.*;

public class RowGetterFactory implements GetterFactory<GettableByIndexData, DatastaxColumnKey> {

    private final HashMap<Class<?>, GetterFactory<GettableByIndexData, DatastaxColumnKey>> getterFactories = new HashMap<Class<?>, GetterFactory<GettableByIndexData, DatastaxColumnKey>>();
    private final DatastaxMapperFactory datastaxMapperFactory;

    private JodaTimeGetterFactory<GettableByIndexData, DatastaxColumnKey> jodaTimeGetterFactory;

    public RowGetterFactory(DatastaxMapperFactory datastaxMapperFactory) {
        this.datastaxMapperFactory = datastaxMapperFactory;
        GetterFactory<GettableByIndexData, DatastaxColumnKey> dateGetterFactory = new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                return (Getter<GettableByIndexData, P>) new DatastaxDateGetter(key.getIndex());
            }
        };
        //IFJAVA8_START
        JavaTimeGetterFactory<GettableByIndexData, DatastaxColumnKey> javaTimeGetterFactory =
                new JavaTimeGetterFactory<GettableByIndexData, DatastaxColumnKey>(dateGetterFactory);
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

        jodaTimeGetterFactory = new JodaTimeGetterFactory<GettableByIndexData, DatastaxColumnKey>(dateGetterFactory);


        getterFactories.put(Short.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass != null && Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericShortGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(short.class, getterFactories.get(Short.class));

        getterFactories.put(Integer.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Integer.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxIntegerGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericIntegerGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(int.class, getterFactories.get(Integer.class));

        getterFactories.put(Long.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Long.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxLongGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericLongGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(long.class, getterFactories.get(Long.class));

        getterFactories.put(Float.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Float.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxFloatGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericFloatGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(float.class, getterFactories.get(Float.class));

        getterFactories.put(Double.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Double.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxDoubleGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericDoubleGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });
        getterFactories.put(double.class, getterFactories.get(Double.class));

        getterFactories.put(BigInteger.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(BigInteger.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxBigIntegerGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericBigIntegerGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });

        getterFactories.put(BigDecimal.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(BigDecimal.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxBigDecimalGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericBigDecimalGetter(key.getIndex(), key.getDateType());
                }
                return null;
            }
        });

    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
        Class<?> targetClass = TypeHelper.toClass(target);
        if (String.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxStringGetter(key.getIndex());
        }
        if (Date.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxDateGetter(key.getIndex());
        }

        if (boolean.class.equals(targetClass) || Boolean.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxBooleanGetter(key.getIndex());
        }

        if (UUID.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxUUIDGetter(key.getIndex());
        }
        if (InetAddress.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxInetAddressGetter(key.getIndex());
        }

        if (TupleValue.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxTupleValueGetter(key.getIndex());
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

        if (Tuples.isTuple(target)) {
            if (key.getDateType() != null && key.getDateType() instanceof TupleType) {
                TupleType tt = (TupleType) key.getDateType();

                List<DataType> typeArguments = tt.getTypeArguments();

                TypeVariable<? extends Class<?>>[] typeParameters = targetClass.getTypeParameters();

                if (typeArguments.size() <= typeParameters.length) {
                    return (Getter<GettableByIndexData, P>) DatastaxTupleGetter.newInstance(datastaxMapperFactory, target, tt, key.getIndex());
                }

            }
        }

        if (TypeHelper.isEnum(target)) {
            final Getter<GettableByIndexData, ? extends Enum> getter = enumGetter(key, TypeHelper.toClass(target));
            if (getter != null) {
                return (Getter<GettableByIndexData, P>)getter;
            }
        }

        final GetterFactory<GettableByIndexData, DatastaxColumnKey> rowGetterFactory = getterFactories.get(targetClass);

        if (rowGetterFactory != null) {
            return rowGetterFactory.newGetter(target, key, columnDefinition);
        }

        final Getter<GettableByIndexData, P> getter = jodaTimeGetterFactory.newGetter(target, key, columnDefinition);

        if (getter != null) {
            return getter;
        }

        if (key.getDateType() != null && key.getDateType() instanceof UserType) {
            UserType ut = (UserType) key.getDateType();
            return (Getter<GettableByIndexData, P>) DatastaxUDTGetter.newInstance(datastaxMapperFactory, target, ut, key.getIndex());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> Getter<GettableByIndexData, E> enumGetter(DatastaxColumnKey key, Class<?> enumClass) {

        if (key.getDateType() != null) {
            final Class<?> javaClass = key.getDateType() != null ? key.getDateType().asJavaClass() : null;
            if (Number.class.isAssignableFrom(javaClass)) {
                return new OrdinalEnumGetter<GettableByIndexData, E>(new DatastaxIntegerGetter(key.getIndex()), (Class<E>)enumClass);
            } else if (String.class.equals(javaClass)) {
                return new StringEnumGetter<GettableByIndexData, E>(new DatastaxStringGetter(key.getIndex()), (Class<E>)enumClass);
            }
        } else {
            return new EnumUnspecifiedTypeGetter<GettableByIndexData, E>(new DatastaxObjectGetter(key.getIndex()), (Class<E>)enumClass);
        }
        return null;
    }
}
