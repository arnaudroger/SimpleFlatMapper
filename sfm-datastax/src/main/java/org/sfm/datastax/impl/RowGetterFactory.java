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

import org.sfm.utils.conv.Converter;
import org.sfm.utils.conv.ConverterFactory;

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
                final Class<?> javaClass = key.getDataType() != null ? key.getDataType().asJavaClass() : null;
                if (javaClass != null && Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericShortGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.put(short.class, getterFactories.get(Short.class));

        getterFactories.put(Integer.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDataType() != null ? key.getDataType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Integer.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxIntegerGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericIntegerGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.put(int.class, getterFactories.get(Integer.class));

        getterFactories.put(Long.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDataType() != null ? key.getDataType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Long.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxLongGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericLongGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.put(long.class, getterFactories.get(Long.class));

        getterFactories.put(Float.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDataType() != null ? key.getDataType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Float.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxFloatGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericFloatGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.put(float.class, getterFactories.get(Float.class));

        getterFactories.put(Double.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDataType() != null ? key.getDataType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(Double.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxDoubleGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericDoubleGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.put(double.class, getterFactories.get(Double.class));

        getterFactories.put(BigInteger.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDataType() != null ? key.getDataType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(BigInteger.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxBigIntegerGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericBigIntegerGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });

        getterFactories.put(BigDecimal.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                final Class<?> javaClass = key.getDataType() != null ? key.getDataType().asJavaClass() : null;
                if (javaClass == null || javaClass.equals(BigDecimal.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxBigDecimalGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericBigDecimalGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });

        getterFactories.put(String.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                if (key.getDataType() == null || String.class.equals(key.getDataType().asJavaClass())) {
                    return (Getter<GettableByIndexData, P>) new DatastaxStringGetter(key.getIndex());
                } else {
                    Getter<GettableByIndexData, ?> getter = RowGetterFactory.this.newGetter(key.getDataType().asJavaClass(), key, columnDefinition);
                    if (getter != null) {
                        return (Getter<GettableByIndexData, P>) new DatastaxToStringGetter(getter);
                    }
                }
                return null;
            }
        });

        getterFactories.put(UUID.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
                if (key.getDataType() == null || UUID.class.equals(key.getDataType().asJavaClass())) {
                    return (Getter<GettableByIndexData, P>) new DatastaxUUIDGetter(key.getIndex());
                } else if (String.class.equals(key.getDataType().asJavaClass())){
                    return (Getter<GettableByIndexData, P>) new DatastaxUUIDFromStringGetter(key.getIndex());
                }
                return null;
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition) {
        Class<?> targetClass = TypeHelper.toClass(target);
        if (Date.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxDateGetter(key.getIndex());
        }

        if (boolean.class.equals(targetClass) || Boolean.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxBooleanGetter(key.getIndex());
        }

        if (InetAddress.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxInetAddressGetter(key.getIndex());
        }

        if (TupleValue.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxTupleValueGetter(key.getIndex());
        }

        if (Collection.class.isAssignableFrom(targetClass)) {

            Type elementType = TypeHelper.getComponentTypeOfListOrArray(target);
            Class<?> dataTypeClass = Object.class;
            Class<?> dataTypeElt = null;
            DataType dtElt = null;
            if (key.getDataType() != null) {
                DataType dataType = key.getDataType();
                dataTypeClass = dataType.asJavaClass();
                if (dataType.isCollection()) {
                    dtElt = key.getDataType().getTypeArguments().get(0);
                    dataTypeElt = dtElt.asJavaClass();
                }
            } else {
                dataTypeElt = TypeHelper.toClass(elementType);
            }

            if (dataTypeElt != null) {
                if (TypeHelper.areEquals(elementType, dataTypeElt)) {
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
                } else {
                    Converter<?, ?> converter = getConverter(elementType, dataTypeElt, dtElt);

                    if (converter != null) {
                        if (Set.class.equals(dataTypeClass)) {
                            if (targetClass.isAssignableFrom(dataTypeClass)) {
                                return new DatastaxSetWithConverterGetter(key.getIndex(), dataTypeElt, converter);
                            }
                        }
                        if (List.class.equals(dataTypeClass)) {
                            if (targetClass.isAssignableFrom(dataTypeClass)) {
                                return new DatastaxListWithConverterGetter(key.getIndex(), dataTypeElt, converter);
                            }
                        }
                    }
                }

            }
        }
        if (Map.class.equals(targetClass)) {
            Tuple2<Type, Type> keyValueTypeOfMap = TypeHelper.getKeyValueTypeOfMap(target);

            Class<?> dtKeyType = null;
            Class<?> dtValueType = null;
            DataType dtKey = null;
            DataType dtValue = null;
            if (key.getDataType() != null) {
                List<DataType> typeArguments = key.getDataType().getTypeArguments();
                if (typeArguments.size() == 2) {
                    dtKey = typeArguments.get(0);
                    dtKeyType = dtKey.asJavaClass();
                    dtValue = typeArguments.get(1);
                    dtValueType = dtValue.asJavaClass();
                }
            } else {
                dtKeyType = TypeHelper.toClass(keyValueTypeOfMap.first());
                dtValueType = TypeHelper.toClass(keyValueTypeOfMap.second());
            }
            if (dtKeyType != null && dtValueType != null) {
                if (TypeHelper.areEquals(keyValueTypeOfMap.first(), dtKeyType)
                        && TypeHelper.areEquals(keyValueTypeOfMap.second(), dtValueType)) {
                    return new DatastaxMapGetter(key.getIndex(), TypeHelper.toClass(keyValueTypeOfMap.first()), TypeHelper.toClass(keyValueTypeOfMap.second()));
                } else {
                    Converter<?, ?> keyConverter = getConverter(keyValueTypeOfMap.first(), dtKeyType, dtKey);
                    Converter<?, ?> valueConverter = getConverter(keyValueTypeOfMap.second(), dtValueType, dtValue);

                    if (keyConverter != null && valueConverter != null) {
                        return new DatastaxMapWithConverterGetter(key.getIndex(), dtKeyType, dtValueType, keyConverter, valueConverter);
                    }
                }
            }
        }

        if (Tuples.isTuple(target)) {
            if (key.getDataType() != null && key.getDataType() instanceof TupleType) {
                TupleType tt = (TupleType) key.getDataType();

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

        if (key.getDataType() != null && key.getDataType() instanceof UserType) {
            UserType ut = (UserType) key.getDataType();
            return (Getter<GettableByIndexData, P>) DatastaxUDTGetter.newInstance(datastaxMapperFactory, target, ut, key.getIndex());
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    private Converter<?, ?> getConverter(Type elementType,  Class<?> dataTypeElt, DataType dtElt) {
        if (dtElt != null) {
            if (UDTValue.class.equals(dataTypeElt)) {
                return new ConverterMapper(DatastaxUDTGetter.newUDTMapper(elementType, (UserType) dtElt, datastaxMapperFactory));
            }
            if (TupleValue.class.equals(dataTypeElt)) {
                return new ConverterMapper(DatastaxTupleGetter.newTupleMapper(elementType, (TupleType) dtElt, datastaxMapperFactory));
            }
        }
        return ConverterFactory.getConverter(dataTypeElt, elementType);
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> Getter<GettableByIndexData, E> enumGetter(DatastaxColumnKey key, Class<?> enumClass) {

        if (key.getDataType() != null) {
            final Class<?> javaClass = key.getDataType() != null ? key.getDataType().asJavaClass() : null;
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
