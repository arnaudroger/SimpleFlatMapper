package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.impl.convert.CalendarToTimestampConverter;
import org.simpleflatmapper.jdbc.impl.convert.UtilDateToTimestampConverter;

//IFJAVA8_START
import org.simpleflatmapper.util.date.time.JavaTimeHelper;
import java.time.*;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaInstantToTimestampConverter;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaLocalDateTimeToTimestampConverter;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaLocalDateToDateConverter;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaLocalTimeToTimeConverter;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaOffsetDateTimeToTimestampConverter;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaOffsetTimeToTimeConverter;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaYearMonthToDateConverter;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaYearToDateConverter;
import org.simpleflatmapper.jdbc.impl.convert.time.JavaZonedDateTimeToTimestampConverter;

//IFJAVA8_END

import org.simpleflatmapper.util.date.joda.JodaTimeHelper;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.IndexedSetter;
import org.simpleflatmapper.reflect.IndexedSetterFactory;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.jdbc.impl.setter.ArrayPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BigDecimalPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BlobPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BooleanPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BooleanPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.BytePreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BytePreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.BytesPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.CharacterPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.CharacterPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.ClobPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ConvertDelegateIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.DatePreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.DoublePreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.DoublePreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.FloatPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.FloatPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.InputStreamPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.IntegerPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.IntegerPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.JodaTimePreparedStatementFactory;
import org.simpleflatmapper.jdbc.impl.setter.LongPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.LongPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.NClobPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ObjectPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.OrdinalEnumPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.PreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.PreparedStatementSetterImpl;
import org.simpleflatmapper.jdbc.impl.setter.ReaderPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.RefPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.RowIdPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.SQLXMLPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ShortPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ShortPreparedStatementSetter;
import org.simpleflatmapper.jdbc.impl.setter.StringEnumPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.StringPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.TimePreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.TimestampPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.URLPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.UUIDBinaryPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.UUIDStringPreparedStatementIndexSetter;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PreparedStatementSetterFactory
        implements
        SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>,
        IndexedSetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>
        {
    private final Map<Class<?>, Factory> factoryPerClass =
            new HashMap<Class<?>, Factory>();

    {
        factoryPerClass.put(boolean.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BooleanPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Boolean.class, factoryPerClass.get(boolean.class));

        factoryPerClass.put(byte.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BytePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Byte.class, factoryPerClass.get(byte.class));

        factoryPerClass.put(char.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new CharacterPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Character.class, factoryPerClass.get(char.class));

        factoryPerClass.put(short.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ShortPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Short.class, factoryPerClass.get(short.class));

        factoryPerClass.put(int.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new IntegerPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Integer.class, factoryPerClass.get(int.class));

        factoryPerClass.put(long.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new LongPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(float.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new FloatPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Float.class, factoryPerClass.get(float.class));

        factoryPerClass.put(double.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new DoublePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Double.class, factoryPerClass.get(double.class));

        factoryPerClass.put(String.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new StringPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Date.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ConvertDelegateIndexSetter<Date, Timestamp>(
                                new TimestampPreparedStatementIndexSetter(),
                                new UtilDateToTimestampConverter()
                        );
                    }
                });
        factoryPerClass.put(Timestamp.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new TimestampPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(java.sql.Date.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new DatePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Time.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new TimePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Calendar.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ConvertDelegateIndexSetter<Calendar, Timestamp>(
                                new TimestampPreparedStatementIndexSetter(),
                                new CalendarToTimestampConverter()
                        );
                    }
                });
        factoryPerClass.put(URL.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new URLPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Ref.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new RefPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(BigDecimal.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BigDecimalPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Array.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ArrayPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(byte[].class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BytesPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(NClob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new NClobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(RowId.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new RowIdPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Blob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BlobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Clob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ClobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(InputStream.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new InputStreamPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Reader.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ReaderPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(SQLXML.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new SQLXMLPreparedStatementIndexSetter();
                    }
                });

        factoryPerClass.put(UUID.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        switch (pm.getColumnKey().getSqlType()) {
                            case Types.BINARY:
                            case Types.VARBINARY:
                            case Types.LONGVARBINARY:
                                return (PreparedStatementIndexSetter<P>) new UUIDBinaryPreparedStatementIndexSetter();
                        }
                        return (PreparedStatementIndexSetter<P>) new UUIDStringPreparedStatementIndexSetter();
                    }
                });
    }

    private final Factory jodaTimeFieldMapperToSourceFactory =
            new JodaTimePreparedStatementFactory();

    //IFJAVA8_START
    private final Factory javaTimeFieldMapperToSourceFactory =
            new Factory() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> PreparedStatementIndexSetter<P> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                    if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalDateTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<java.time.LocalDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexSetter(),
                                        new JavaLocalDateTimeToTimestampConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition().properties())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalDate.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<java.time.LocalDate, java.sql.Date>(
                                        new DatePreparedStatementIndexSetter(),
                                        new JavaLocalDateToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition().properties())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<java.time.LocalTime, Time>(
                                        new TimePreparedStatementIndexSetter(),
                                        new JavaLocalTimeToTimeConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition().properties())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.Instant.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<java.time.Instant, Timestamp>(
                                        new TimestampPreparedStatementIndexSetter(),
                                        new JavaInstantToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.ZonedDateTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<ZonedDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexSetter(),
                                        new JavaZonedDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.OffsetDateTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<OffsetDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexSetter(),
                                        new JavaOffsetDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.OffsetTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<OffsetTime, Time>(
                                        new TimePreparedStatementIndexSetter(),
                                        new JavaOffsetTimeToTimeConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.YearMonth.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<java.time.YearMonth, java.sql.Date>(
                                        new DatePreparedStatementIndexSetter(),
                                        new JavaYearMonthToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition().properties())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.Year.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<Year, java.sql.Date>(
                                        new DatePreparedStatementIndexSetter(),
                                        new JavaYearToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition().properties())));
                    }

                    return null;
                }
            };
    //IFJAVA8_END

    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
        int columnIndex = pm.getColumnKey().getIndex();

        Type type = pm.getPropertyMeta().getPropertyType();

            Class<?> clazz = TypeHelper.toBoxedClass(type);

        if (Boolean.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new BooleanPreparedStatementSetter(columnIndex);
        } else if (Byte.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new BytePreparedStatementSetter(columnIndex);
        } else if (Character.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new CharacterPreparedStatementSetter(columnIndex);
        } else if (Short.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new ShortPreparedStatementSetter(columnIndex);
        } else if (Integer.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new IntegerPreparedStatementSetter(columnIndex);
        } else if (Long.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new LongPreparedStatementSetter(columnIndex);
        } else if (Double.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new DoublePreparedStatementSetter(columnIndex);
        } else if (Float.class.equals(clazz)) {
            return (Setter<PreparedStatement, P>) new FloatPreparedStatementSetter(columnIndex);
        }

        IndexedSetter<PreparedStatement, P> setter = getIndexedSetter(pm);

        if (setter != null) {
            return new PreparedStatementSetterImpl<P>(columnIndex, setter);
        } else return null;
    }

    @Override
    public <T> IndexedSetter<PreparedStatement, T> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> arg) {
        Type propertyType = arg.getPropertyMeta().getPropertyType();
        IndexedSetter<PreparedStatement, T> setter = null;
        Factory setterFactory = this.factoryPerClass.get(TypeHelper.toClass(propertyType));
        if (setterFactory != null) {
            setter = setterFactory.<T>getIndexedSetter(arg);
        }

        if (TypeHelper.isEnum(propertyType)) {
            switch (arg.getColumnKey().getSqlType()) {
                case Types.NUMERIC:
                case Types.BIGINT:
                case Types.INTEGER:
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.FLOAT:
                case Types.SMALLINT:
                case Types.REAL:
                case Types.TINYINT:
                   setter = (IndexedSetter<PreparedStatement, T>) new OrdinalEnumPreparedStatementIndexSetter();
                    break;
                default:
                    setter = (IndexedSetter<PreparedStatement, T>) new StringEnumPreparedStatementIndexSetter();
            }
        }

        if (setter == null && JodaTimeHelper.isJoda(propertyType)) {
            setter = jodaTimeFieldMapperToSourceFactory.getIndexedSetter(arg);
        }

        //IFJAVA8_START
        if (setter == null) {
            setter = javaTimeFieldMapperToSourceFactory.getIndexedSetter(arg);
        }
        //IFJAVA8_END

        if (setter == null && TypeHelper.isAssignable(SQLData.class, propertyType)) {
            setter = (IndexedSetter<PreparedStatement, T>) new ObjectPreparedStatementIndexSetter();
        }
        return setter;
    }

    public interface Factory
            extends IndexedSetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> {
    }

}
