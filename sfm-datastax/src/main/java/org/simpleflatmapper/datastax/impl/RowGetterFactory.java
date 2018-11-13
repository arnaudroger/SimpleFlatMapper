package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.*;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.converter.EmptyContextFactoryBuilder;
import org.simpleflatmapper.reflect.getter.GetterFactoryRegistry;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.datastax.DatastaxMapperFactory;
import org.simpleflatmapper.reflect.getter.EnumUnspecifiedTypeGetter;
import org.simpleflatmapper.reflect.getter.OrdinalEnumGetter;
import org.simpleflatmapper.reflect.getter.StringEnumGetter;
import org.simpleflatmapper.reflect.Getter;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;


import org.simpleflatmapper.converter.ContextualConverter;

import org.simpleflatmapper.datastax.impl.getter.DatastaxBigDecimalGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxBigIntegerGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxBooleanGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxByteGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxDateGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxDoubleGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxFloatGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxGenericBigDecimalGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxGenericBigIntegerGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxGenericByteGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxGenericDoubleGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxGenericFloatGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxGenericIntegerGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxGenericLongGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxGenericShortGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxInetAddressGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxIntegerGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxListGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxListWithConverterGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxLongGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxMapGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxMapWithConverterGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxObjectGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxSetGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxSetWithConverterGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxShortGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxStringGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxTimeGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxTimestampGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxToStringGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxTupleGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxTupleValueGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxUDTGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxUUIDFromStringGetter;
import org.simpleflatmapper.datastax.impl.getter.DatastaxUUIDGetter;
import org.simpleflatmapper.util.TupleHelper;
import org.simpleflatmapper.util.TypeHelper;

import java.util.*;

public class RowGetterFactory implements GetterFactory<GettableByIndexData, DatastaxColumnKey> {

    private final GetterFactoryRegistry<GettableByIndexData, DatastaxColumnKey> getterFactories = new GetterFactoryRegistry<GettableByIndexData, DatastaxColumnKey>();
    private final DatastaxMapperFactory datastaxMapperFactory;

    public RowGetterFactory(DatastaxMapperFactory datastaxMapperFactory) {
        this.datastaxMapperFactory = datastaxMapperFactory;
        GetterFactory<GettableByIndexData, DatastaxColumnKey> dateGetterFactory = new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                return (Getter<GettableByIndexData, P>) new DatastaxTimestampGetter(key.getIndex());
            }
        };
        getterFactories.put(Byte.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;

                if (javaClass == null || javaClass.equals(Byte.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxByteGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericByteGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.mapFromTo(byte.class, Byte.class);

        getterFactories.put(Short.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;

                if (javaClass == null || javaClass.equals(Short.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxShortGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericShortGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.mapFromTo(short.class, Short.class);

        getterFactories.put(Integer.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;
                if (javaClass == null || javaClass.equals(Integer.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxIntegerGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericIntegerGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.mapFromTo(int.class, Integer.class);

        getterFactories.put(Long.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;
                if (key.getDataType() == DataType.time()) {
                    return (Getter<GettableByIndexData, P>) new DatastaxTimeGetter(key.getIndex());
                } else if (javaClass == null || javaClass.equals(Long.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxLongGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericLongGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.mapFromTo(long.class, Long.class);

        getterFactories.put(Float.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;
                if (javaClass == null || javaClass.equals(Float.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxFloatGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericFloatGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.mapFromTo(float.class, Float.class);

        getterFactories.put(Double.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;
                if (javaClass == null || javaClass.equals(Double.class)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxDoubleGetter(key.getIndex());
                } else if (Number.class.isAssignableFrom(javaClass)) {
                    return (Getter<GettableByIndexData, P>) new DatastaxGenericDoubleGetter(key.getIndex(), key.getDataType());
                }
                return null;
            }
        });
        getterFactories.mapFromTo(double.class, Double.class);

        getterFactories.put(BigInteger.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;
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
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;
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
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                if (key.getDataType() == null || String.class.equals(DataTypeHelper.asJavaClass(key.getDataType()))) {
                    return (Getter<GettableByIndexData, P>) new DatastaxStringGetter(key.getIndex());
                } else {
                    Getter<GettableByIndexData, ?> getter = RowGetterFactory.this.newGetter(DataTypeHelper.asJavaClass(key.getDataType()), key, properties);
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
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                if (key.getDataType() == null || UUID.class.equals(DataTypeHelper.asJavaClass(key.getDataType()))) {
                    return (Getter<GettableByIndexData, P>) new DatastaxUUIDGetter(key.getIndex());
                } else if (String.class.equals(DataTypeHelper.asJavaClass(key.getDataType()))){
                    return (Getter<GettableByIndexData, P>) new DatastaxUUIDFromStringGetter(key.getIndex());
                }
                return null;
            }
        });

        getterFactories.put(LocalDate.class, new GetterFactory<GettableByIndexData, DatastaxColumnKey>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
                return (Getter<GettableByIndexData, P>) new DatastaxDateGetter(key.getIndex());
            }
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Getter<GettableByIndexData, P> newGetter(Type target, DatastaxColumnKey key, Object... properties) {
        Class<?> targetClass = TypeHelper.toClass(target);
        if (Date.class.equals(targetClass)) {
            return (Getter<GettableByIndexData, P>) new DatastaxTimestampGetter(key.getIndex());
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
                dataTypeClass = DataTypeHelper.asJavaClass(dataType);
                if (dataType.isCollection()) {
                    dtElt = key.getDataType().getTypeArguments().get(0);
                    dataTypeElt = DataTypeHelper.asJavaClass(dtElt);
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
                    ContextualConverter<?, ?> converter = getConverter(elementType, dataTypeElt, dtElt);

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
            TypeHelper.MapEntryTypes keyValueTypeOfMap = TypeHelper.getKeyValueTypeOfMap(target);

            Class<?> dtKeyType = null;
            Class<?> dtValueType = null;
            DataType dtKey = null;
            DataType dtValue = null;
            if (key.getDataType() != null) {
                List<DataType> typeArguments = key.getDataType().getTypeArguments();
                if (typeArguments.size() == 2) {
                    dtKey = typeArguments.get(0);
                    dtKeyType = DataTypeHelper.asJavaClass(dtKey);
                    dtValue = typeArguments.get(1);
                    dtValueType = DataTypeHelper.asJavaClass(dtValue);
                }
            } else {
                dtKeyType = TypeHelper.toClass(keyValueTypeOfMap.getKeyType());
                dtValueType = TypeHelper.toClass(keyValueTypeOfMap.getValueType());
            }
            if (dtKeyType != null && dtValueType != null) {
                if (TypeHelper.areEquals(keyValueTypeOfMap.getKeyType(), dtKeyType)
                        && TypeHelper.areEquals(keyValueTypeOfMap.getValueType(), dtValueType)) {
                    return new DatastaxMapGetter(key.getIndex(), TypeHelper.toClass(keyValueTypeOfMap.getKeyType()), TypeHelper.toClass(keyValueTypeOfMap.getValueType()));
                } else {
                    ContextualConverter<?, ?> keyConverter = getConverter(keyValueTypeOfMap.getKeyType(), dtKeyType, dtKey);
                    ContextualConverter<?, ?> valueConverter = getConverter(keyValueTypeOfMap.getValueType(), dtValueType, dtValue);

                    if (keyConverter != null && valueConverter != null) {
                        return new DatastaxMapWithConverterGetter(key.getIndex(), dtKeyType, dtValueType, keyConverter, valueConverter);
                    }
                }
            }
        }

        if (TupleHelper.isTuple(target)) {
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
            final Getter<GettableByIndexData, ? extends Enum> getter = enumGetter(key, TypeHelper.<Enum<?>>toClass(target));
            if (getter != null) {
                return (Getter<GettableByIndexData, P>)getter;
            }
        }

        if (key.getDataType() != null && key.getDataType() instanceof UserType) {
            UserType ut = (UserType) key.getDataType();
            return (Getter<GettableByIndexData, P>) DatastaxUDTGetter.newInstance(datastaxMapperFactory, target, ut, key.getIndex());
        }

        Getter<GettableByIndexData, P> getter = getterFromFactories(target, key,  properties);

        if (getter != null) return getter;

        return null;
    }

    private <P> Getter<GettableByIndexData, P> getterFromFactories(Type target, DatastaxColumnKey key, Object[] properties) {
        final GetterFactory<GettableByIndexData, DatastaxColumnKey> rowGetterFactory = getterFactories.findFactoryFor(target);

        if (rowGetterFactory != null) {
            Getter<GettableByIndexData, P> getter = rowGetterFactory.newGetter(target, key, properties);
            if (getter != null) return getter;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private ContextualConverter<?, ?> getConverter(Type elementType, Class<?> dataTypeElt, DataType dtElt) {
        if (dtElt != null) {
            if (UDTValue.class.equals(dataTypeElt)) {
                return new ConverterMapper(DatastaxUDTGetter.newUDTMapper(elementType, (UserType) dtElt, datastaxMapperFactory));
            }
            if (TupleValue.class.equals(dataTypeElt)) {
                return new ConverterMapper(DatastaxTupleGetter.newTupleMapper(elementType, (TupleType) dtElt, datastaxMapperFactory));
            }
        }
        return ConverterService.getInstance().findConverter(dataTypeElt, elementType, EmptyContextFactoryBuilder.INSTANCE);
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> Getter<GettableByIndexData, E> enumGetter(DatastaxColumnKey key, Class<?> enumClass) {

        if (key.getDataType() != null) {
            final Class<?> javaClass = key.getDataType() != null ? DataTypeHelper.asJavaClass(key.getDataType()) : null;
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
