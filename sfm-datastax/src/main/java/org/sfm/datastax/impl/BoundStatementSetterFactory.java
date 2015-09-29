package org.sfm.datastax.impl;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.DataType;
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
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class BoundStatementSetterFactory implements SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> {
    private final Map<Class<?>, SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>> factoryPerClass =
            new HashMap<Class<?>, SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>>();

    {
        factoryPerClass.put(long.class, new SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<BoundStatement, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<BoundStatement, P>) new LongBoundStatementSetter(arg.getColumnKey().getIndex());
            }
        });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(String.class, new SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<BoundStatement, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<BoundStatement, P>) new StringBoundStatementSetter(arg.getColumnKey().getIndex());
            }
        });

        factoryPerClass.put(Date.class, new SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> Setter<BoundStatement, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
                return (Setter<BoundStatement, P>) new DateBoundStatementSetter(arg.getColumnKey().getIndex());
            }
        });

    }

    private final SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> jodaTimeFieldMapperToSourceFactory =
            new SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> Setter<BoundStatement, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> pm) {
                    return null;
                }
            };

    //IFJAVA8_START
    private final SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> javaTimeFieldMapperToSourceFactory =
            new SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> Setter<BoundStatement, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> pm) {
                    return null;
                }
            };
    //IFJAVA8_END

    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<BoundStatement, P> getSetter(PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> arg) {
        Setter<BoundStatement, P> setter = null;
        Type propertyType = arg.getPropertyMeta().getPropertyType();
        SetterFactory<BoundStatement, PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>>> setterFactory = this.factoryPerClass.get(TypeHelper.toClass(propertyType));
        if (setterFactory != null) {
            setter = setterFactory.getSetter(arg);
        }

        if (TypeHelper.isEnum(propertyType)) {
            DataType dataType = arg.getColumnKey().getDataType();
            if (dataType != null && dataType.asJavaClass().equals(String.class)) {
                return (Setter<BoundStatement, P>) new StringEnumBoundStatementSetter(arg.getColumnKey().getIndex());
            } else {
                return (Setter<BoundStatement, P>) new OrdinalEnumBoundStatementSetter(arg.getColumnKey().getIndex());
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
