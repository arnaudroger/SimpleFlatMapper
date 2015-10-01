package org.sfm.datastax.impl;

import com.datastax.driver.core.*;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.datastax.impl.setter.*;
import org.sfm.jdbc.impl.convert.CalendarToTimestampConverter;
import org.sfm.jdbc.impl.convert.UtilDateToTimestampConverter;
import org.sfm.jdbc.impl.convert.joda.*;

//IFJAVA8_START
import org.sfm.jdbc.impl.convert.time.*;
import org.sfm.map.MapperConfig;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.column.time.JavaTimeHelper;
//IFJAVA8_END

import org.sfm.map.column.joda.JodaHelper;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.setter.ConvertDelegateSetter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;
import org.sfm.tuples.Tuples;
import org.sfm.utils.conv.Converter;
import org.sfm.utils.conv.ConverterFactory;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


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

    private final SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> jodaTimeFieldMapperToSourceFactory =
            new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> pm) {
                    return null;
                }
            };

    //IFJAVA8_START
    private final SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> javaTimeFieldMapperToSourceFactory =
            new SetterFactory<SettableByIndexData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> pm) {
                    return null;
                }
            };
    //IFJAVA8_END

    public SettableDataSetterFactory(MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> mapperConfig, ReflectionService reflectionService) {
        this.mapperConfig = mapperConfig;
        this.reflectionService = reflectionService;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<SettableByIndexData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
        Setter<SettableByIndexData, P> setter = null;
        Type propertyType = arg.getPropertyMeta().getPropertyType();

        Type type = arg.getColumnKey().getDataType().asJavaClass();
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
                Converter<?, ?> converter = ConverterFactory.getConverter(TypeHelper.toClass(propertyType), type);
                if (converter != null) {
                    setter = (Setter<SettableByIndexData, P>) new ConvertDelegateSetter<SettableByIndexData, Object, P>(setter, (Converter<Object, P>) converter);
                } else {
                    setter = null;
                }
            }
        }

        if (setter == null) {
            if (Tuples.isTuple(propertyType) && TypeHelper.areEquals(type, TupleValue.class)) {
                setter = (Setter<SettableByIndexData, P>) TupleSettableDataSetter.newInstance(propertyType, (TupleType)arg.getColumnKey().getDataType(), arg.getColumnKey().getIndex(), mapperConfig, reflectionService);
            }
        }

        if (setter == null) {
            setter = jodaTimeFieldMapperToSourceFactory.getSetter(arg);
        }

        //IFJAVA8_START
        if (setter == null) {
            setter = javaTimeFieldMapperToSourceFactory.getSetter(arg);
        }
        //IFJAVA8_END


        return setter;
    }
}
