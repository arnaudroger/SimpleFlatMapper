package org.sfm.jdbc.impl;

import org.joda.time.*;
import org.joda.time.Instant;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.convert.CalendarToTimestampConverter;
import org.sfm.jdbc.impl.convert.UtilDateToTimestampConverter;
import org.sfm.jdbc.impl.convert.joda.*;

//IFJAVA8_START
import org.sfm.jdbc.impl.convert.time.*;
import org.sfm.map.column.time.JavaTimeHelper;
import java.time.*;
//IFJAVA8_END

import org.sfm.jdbc.impl.setter.*;
import org.sfm.map.column.joda.JodaHelper;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.setter.ConvertDelegateSetter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;

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

public class PreparedStatementSetterFactory implements SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> {
    private final Map<Class<?>, SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>> factoryPerClass =
            new HashMap<Class<?>, SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>>();

    {
        factoryPerClass.put(boolean.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new BooleanPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Boolean.class, factoryPerClass.get(boolean.class));

        factoryPerClass.put(byte.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new BytePreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Byte.class, factoryPerClass.get(byte.class));

        factoryPerClass.put(char.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new CharacterPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Character.class, factoryPerClass.get(char.class));

        factoryPerClass.put(short.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new ShortPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Short.class, factoryPerClass.get(short.class));

        factoryPerClass.put(int.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new IntegerPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Integer.class, factoryPerClass.get(int.class));

        factoryPerClass.put(long.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new LongPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(float.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new FloatPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Float.class, factoryPerClass.get(float.class));

        factoryPerClass.put(double.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new DoublePreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Double.class, factoryPerClass.get(double.class));

        factoryPerClass.put(String.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new StringPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Date.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new ConvertDelegateSetter<PreparedStatement, Date, Timestamp>(
                                new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                new UtilDateToTimestampConverter()
                        );
                    }
                });
        factoryPerClass.put(Timestamp.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(java.sql.Date.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new DatePreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Time.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new TimePreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Calendar.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new ConvertDelegateSetter<PreparedStatement, Calendar, Timestamp>(
                                new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                new CalendarToTimestampConverter()
                        );
                    }
                });
        factoryPerClass.put(URL.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new URLPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Ref.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new RefPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(BigDecimal.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new BigDecimalPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Array.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new ArrayPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(byte[].class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new BytesPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(NClob.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new NClobPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(RowId.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new RowIdPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Blob.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new BlobPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Clob.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new ClobPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(InputStream.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new InputStreamPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(Reader.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new ReaderPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
        factoryPerClass.put(SQLXML.class,
                new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (Setter<PreparedStatement, P>) new SQLXMLPreparedStatementSetter(pm.getColumnKey().getIndex());
                    }
                });
    }

    private final SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> jodaTimeFieldMapperToSourceFactory =
            new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                    if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.DateTime.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, DateTime, Timestamp>(
                                        new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JodaDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalDateTime.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, LocalDateTime, Timestamp>(
                                        new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JodaLocalDateTimeToTimestampConverter(JodaHelper.getDateTimeZone(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalDate.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, LocalDate, java.sql.Date>(
                                        new DatePreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JodaLocalDateToDateConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalTime.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, LocalTime, Time>(
                                        new TimePreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JodaLocalTimeToTimeConverter(JodaHelper.getDateTimeZone(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.Instant.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, Instant, Timestamp>(
                                        new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JodaInstantToTimestampConverter());
                    }
                    return null;
                }
            };

    //IFJAVA8_START
    private final SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> javaTimeFieldMapperToSourceFactory =
            new SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>>() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                    if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalDateTime.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, java.time.LocalDateTime, Timestamp>(
                                        new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaLocalDateTimeToTimestampConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalDate.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, java.time.LocalDate, java.sql.Date>(
                                        new DatePreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaLocalDateToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalTime.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, java.time.LocalTime, Time>(
                                        new TimePreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaLocalTimeToTimeConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.Instant.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, java.time.Instant, Timestamp>(
                                        new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaInstantToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.ZonedDateTime.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, ZonedDateTime, Timestamp>(
                                        new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaZonedDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.OffsetDateTime.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, OffsetDateTime, Timestamp>(
                                        new TimestampPreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaOffsetDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.OffsetTime.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, OffsetTime, Time>(
                                        new TimePreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaOffsetTimeToTimeConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.YearMonth.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, java.time.YearMonth, java.sql.Date>(
                                        new DatePreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaYearMonthToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.Year.class)) {
                        return (Setter<PreparedStatement, P>)
                                new ConvertDelegateSetter<PreparedStatement, Year, java.sql.Date>(
                                        new DatePreparedStatementSetter(pm.getColumnKey().getIndex()),
                                        new JavaYearToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    }

                    return null;
                }
            };
    //IFJAVA8_END

    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> arg) {
        Setter<PreparedStatement, P> setter = null;
        Type propertyType = arg.getPropertyMeta().getPropertyType();
        SetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>>> setterFactory = this.factoryPerClass.get(TypeHelper.toClass(propertyType));
        if (setterFactory != null) {
            setter = setterFactory.getSetter(arg);
        }

        if (TypeHelper.isEnum(propertyType)) {
            switch (arg.getColumnKey().getSqlType()) {
                case Types.VARCHAR:
                case Types.CHAR:
                case Types.CLOB:
                case Types.LONGNVARCHAR:
                case Types.LONGVARCHAR:
                case Types.NCHAR:
                case Types.NCLOB:
                case Types.NVARCHAR:
                    return (Setter<PreparedStatement, P>) new StringEnumPreparedStatementSetter(arg.getColumnKey().getIndex());
            }
            return (Setter<PreparedStatement, P>) new OrdinalEnumPreparedStatementSetter(arg.getColumnKey().getIndex());
        }

        if (setter == null) {
            setter = jodaTimeFieldMapperToSourceFactory.getSetter(arg);
        }

        //IFJAVA8_START
        if (setter == null) {
            setter = javaTimeFieldMapperToSourceFactory.getSetter(arg);
        }
        //IFJAVA8_END

        if (setter == null && TypeHelper.isAssignable(SQLData.class, propertyType)) {
            setter = (Setter<PreparedStatement, P>) new ObjectPreparedStatementSetter(arg.getColumnKey().getIndex());
        }


        return setter;
    }
}
