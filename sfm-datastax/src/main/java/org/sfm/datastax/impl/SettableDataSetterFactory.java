package org.sfm.datastax.impl;

import com.datastax.driver.core.SettableData;
import com.datastax.driver.core.DataType;
import com.datastax.driver.core.TupleValue;
import org.sfm.datastax.DatastaxColumnKey;
import org.sfm.datastax.impl.setter.*;
import org.sfm.jdbc.impl.convert.CalendarToTimestampConverter;
import org.sfm.jdbc.impl.convert.UtilDateToTimestampConverter;
import org.sfm.jdbc.impl.convert.joda.*;

//IFJAVA8_START
import org.sfm.jdbc.impl.convert.time.*;
import org.sfm.map.column.time.JavaTimeHelper;
//IFJAVA8_END

import org.sfm.map.column.joda.JodaHelper;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.setter.ConvertDelegateSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;
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
        SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>

{
    private final Map<Class<?>, SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>> factoryPerClass =
            new HashMap<Class<?>, SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>>();

    {
        factoryPerClass.put(int.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new IntSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Integer.class, factoryPerClass.get(int.class));

        factoryPerClass.put(long.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new LongSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(float.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new FloatSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Float.class, factoryPerClass.get(float.class));

        factoryPerClass.put(double.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new DoubleSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Double.class, factoryPerClass.get(double.class));


        factoryPerClass.put(String.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new StringSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(Date.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new DateSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(UUID.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new UUIDSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(BigDecimal.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new BigDecimalSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(BigInteger.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new BigIntegerSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(InetAddress.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new InetAddressSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(TupleValue.class, new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<SettableData, P>) new TupleValueSettableDataSetter(arg.getColumnKey().getIndex());
            }
        });
    }

    private final SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> jodaTimeFieldMapperToSourceFactory =
            new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> pm) {
                    return null;
                }
            };

    //IFJAVA8_START
    private final SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> javaTimeFieldMapperToSourceFactory =
            new SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> pm) {
                    return null;
                }
            };
    //IFJAVA8_END


    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<SettableData, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
        Setter<SettableData, P> setter = null;
        Type propertyType = arg.getPropertyMeta().getPropertyType();

        Type type = arg.getColumnKey().getDataType().asJavaClass();
        if (type == null) {
            type = propertyType;
        }

        if (TypeHelper.isEnum(propertyType)) {
            if (TypeHelper.isClass(type, String.class)) {
                return (Setter<SettableData, P>) new StringEnumSettableDataSetter(arg.getColumnKey().getIndex());
            } else {
                return (Setter<SettableData, P>) new OrdinalEnumSettableDataSetter(arg.getColumnKey().getIndex());
            }
        }

        SetterFactory<SettableData, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> setterFactory =
                this.factoryPerClass.get(TypeHelper.toClass(type));

        if (setterFactory != null) {
            setter = setterFactory.getSetter(arg);

            if (!TypeHelper.areEquals(TypeHelper.toBoxedClass(type), TypeHelper.toBoxedClass(propertyType))) {
                Converter<?, ?> converter = ConverterFactory.getConverter(TypeHelper.toClass(propertyType), type);
                if (converter != null) {
                    setter = (Setter<SettableData, P>) new ConvertDelegateSetter<SettableData, Object, P>(setter, (Converter<Object, P>) converter);
                } else {
                    setter = null;
                }
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
