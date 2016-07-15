package org.sfm.datastax.impl;

import com.datastax.driver.core.*;
import org.sfm.datastax.DataTypeHelper;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.datastax.impl.setter.*;

import org.sfm.map.Mapper;
import org.sfm.map.MapperConfig;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.setter.ConvertDelegateSetter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.conv.Converter;
import org.sfm.utils.conv.ConverterFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;

import java.util.*;


public class SettableDataSetterFactory
        implements
        SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>

{
    private final Map<Class<?>, SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>> factoryPerClass =
            new HashMap<Class<?>, SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>>();
    private final MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> mapperConfig;
    private final ReflectionService reflectionService;

    {
        factoryPerClass.put(int.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new IntSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Integer.class, factoryPerClass.get(int.class));

        factoryPerClass.put(long.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new LongSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(float.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new FloatSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Float.class, factoryPerClass.get(float.class));

        factoryPerClass.put(double.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new DoubleSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Double.class, factoryPerClass.get(double.class));


        factoryPerClass.put(String.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new StringSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(Date.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new DateSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(UUID.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new UUIDSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(BigDecimal.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new BigDecimalSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(BigInteger.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new BigIntegerSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(InetAddress.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new InetAddressSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(TupleValue.class, new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableByIndexData, P>) new TupleValueSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
    }

    public SettableDataSetterFactory(MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> mapperConfig, ReflectionService reflectionService) {
        this.mapperConfig = mapperConfig;
        this.reflectionService = reflectionService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
        Setter<SettableByIndexData, P> setter = null;

        Type propertyType = arg.getPropertyMeta().getPropertyType();

        final DataType dataType = arg.getColumnKey().getDataType();
        Type type = dataType != null ? DataTypeHelper.asJavaClass(dataType) : null;
        if (type == null) {
            type = propertyType;
        }

        if (TypeHelper.isEnum(propertyType)) {
            if (TypeHelper.isClass(type, String.class)) {
                return (Setter<SettableByIndexData, P>) new StringEnumSettableDataSetter(arg.getColumnKey().getIndex());
            } else {
                return (Setter<SettableByIndexData, P>) new OrdinalEnumSettableDataSetter(arg.getColumnKey().getIndex());
            }
        }

        SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> setterFactory =
                this.factoryPerClass.get(TypeHelper.toClass(type));

        if (setterFactory != null) {
            setter = setterFactory.getSetter(arg);

            if (!TypeHelper.areEquals(TypeHelper.toBoxedClass(type), TypeHelper.toBoxedClass(propertyType))) {
                Converter<?, ?> converter = getConverter(propertyType, TypeHelper.toClass(type), dataType, arg.getColumnDefinition());
                if (converter != null) {
                    setter = (Setter<SettableByIndexData, P>) new ConvertDelegateSetter<SettableByIndexData, Object, P>(setter, (Converter<Object, P>) converter);
                } else {
                    setter = null;
                }
            }
        }

        if (setter == null && dataType != null) {
            if (dataType instanceof UserType) {
                if (propertyType.equals(UDTValue.class)) {
                    setter = (Setter<SettableByIndexData, P>) new UDTValueSettableDataSetter(arg.getColumnKey().getIndex());
                } else {
                    setter = (Setter<SettableByIndexData, P>) UDTObjectSettableDataSetter.newInstance(propertyType, (UserType) dataType, arg.getColumnKey().getIndex(),  mapperConfig, reflectionService);
                }
            } else if (TypeHelper.isAssignable(List.class, type) && TypeHelper.isAssignable(List.class, propertyType)) {

                DataType dataTypeElt = dataType.getTypeArguments().get(0);
                Class<?> dEltType = DataTypeHelper.asJavaClass(dataTypeElt);
                Type lEltType = TypeHelper.getComponentTypeOfListOrArray(propertyType);
                if (TypeHelper.areEquals(lEltType, dEltType)) {
                    setter = new ListSettableDataSetter(arg.getColumnKey().getIndex());
                } else {
                    Converter<?, ?> converter = getConverter(lEltType, dEltType, dataTypeElt, arg.getColumnDefinition());
                    if (converter != null) {
                        setter = new ListWithConverterSettableDataSetter(arg.getColumnKey().getIndex(), converter);
                    }
                }
            } else if (TypeHelper.isAssignable(Set.class, type) && TypeHelper.isAssignable(Set.class, propertyType)) {

                DataType dataTypeElt = dataType.getTypeArguments().get(0);
                Class<?> dEltType = DataTypeHelper.asJavaClass(dataTypeElt);
                Type lEltType = TypeHelper.getComponentTypeOfListOrArray(propertyType);
                if (TypeHelper.areEquals(lEltType, dEltType)) {
                    setter = new SetSettableDataSetter(arg.getColumnKey().getIndex());
                } else {
                    Converter<?, ?> converter = getConverter(lEltType, dEltType, dataTypeElt, arg.getColumnDefinition());
                    if (converter != null) {
                        setter = new SetWithConverterSettableDataSetter(arg.getColumnKey().getIndex(), converter);
                    }
                }
            } else if (TypeHelper.isAssignable(Map.class, type) && TypeHelper.isAssignable(Map.class, propertyType)) {

                DataType dtKeyType = dataType.getTypeArguments().get(0);
                DataType dtValueType = dataType.getTypeArguments().get(1);

                Tuple2<Type, Type> keyValueTypeOfMap = TypeHelper.getKeyValueTypeOfMap(propertyType);

                if (areSame(dtKeyType, keyValueTypeOfMap.getElement0()) && areSame(dtValueType, keyValueTypeOfMap.getElement1())) {
                    setter = new MapSettableDataSetter(arg.getColumnKey().getIndex());
                } else {
                    setter = new MapWithConverterSettableDataSetter(arg.getColumnKey().getIndex(),
                            getConverter(keyValueTypeOfMap.getElement0(), DataTypeHelper.asJavaClass(dtKeyType), dtKeyType, arg.getColumnDefinition()),
                            getConverter(keyValueTypeOfMap.getElement1(), DataTypeHelper.asJavaClass(dtValueType), dtValueType, arg.getColumnDefinition())
                            );
                }
            }

        }

        return setter;
    }

    private boolean areSame(DataType dtKeyType, Type element0) {
        return TypeHelper.areEquals(element0, DataTypeHelper.asJavaClass(dtKeyType));
    }

    @SuppressWarnings("unchecked")
    private Converter<?, ?> getConverter(Type elementType, Class<?> dataTypeElt, DataType dtElt, ColumnDefinition<DatastaxColumnKey, ?> columnDefinition) {
        if (dtElt != null) {
            if (UDTValue.class.equals(dataTypeElt)) {
                Mapper mapper = UDTObjectSettableDataSetter.newUDTMapper(elementType, (UserType) dtElt, mapperConfig, reflectionService);
                return new ConverterToUDTValueMapper(mapper, (UserType) dtElt);
            }
            if (TupleValue.class.equals(dataTypeElt)) {
                Mapper mapper = TupleValueSettableDataSetter.newTupleMapper(elementType, (TupleType) dtElt, mapperConfig, reflectionService);
                return new ConverterToTupleValueMapper(mapper, (TupleType) dtElt);
            }
        }
        return ConverterFactory.getConverter(TypeHelper.toClass(elementType), dataTypeElt, columnDefinition);
    }
}
