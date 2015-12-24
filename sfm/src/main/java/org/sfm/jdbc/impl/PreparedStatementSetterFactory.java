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
    private final Map<Class<?>, Factory> factoryPerClass =
            new HashMap<Class<?>, Factory>();

    {
        factoryPerClass.put(boolean.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new BooleanPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Boolean.class, factoryPerClass.get(boolean.class));

        factoryPerClass.put(byte.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new BytePreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Byte.class, factoryPerClass.get(byte.class));

        factoryPerClass.put(char.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new CharacterPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Character.class, factoryPerClass.get(char.class));

        factoryPerClass.put(short.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new ShortPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Short.class, factoryPerClass.get(short.class));

        factoryPerClass.put(int.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new IntegerPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Integer.class, factoryPerClass.get(int.class));

        factoryPerClass.put(long.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new LongPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(float.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new FloatPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Float.class, factoryPerClass.get(float.class));

        factoryPerClass.put(double.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new DoublePreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Double.class, factoryPerClass.get(double.class));

        factoryPerClass.put(String.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new StringPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Date.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new ConvertDelegateIndexedSetter<Date, Timestamp>(
                                new TimestampPreparedStatementIndexedSetter(),
                                new UtilDateToTimestampConverter()
                        );
                    }
                });
        factoryPerClass.put(Timestamp.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new TimestampPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(java.sql.Date.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new DatePreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Time.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new TimePreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Calendar.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new ConvertDelegateIndexedSetter<Calendar, Timestamp>(
                                new TimestampPreparedStatementIndexedSetter(),
                                new CalendarToTimestampConverter()
                        );
                    }
                });
        factoryPerClass.put(URL.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new URLPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Ref.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new RefPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(BigDecimal.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new BigDecimalPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Array.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new ArrayPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(byte[].class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new BytesPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(NClob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new NClobPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(RowId.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new RowIdPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Blob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new BlobPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Clob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new ClobPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(InputStream.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new InputStreamPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(Reader.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new ReaderPreparedStatementIndexedSetter();
                    }
                });
        factoryPerClass.put(SQLXML.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PrepareStatementIndexedSetter<P>) new SQLXMLPreparedStatementIndexedSetter();
                    }
                });
    }

    private final Factory jodaTimeFieldMapperToSourceFactory =
            new Factory() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                    if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.DateTime.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<DateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexedSetter(),
                                        new JodaDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalDateTime.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<LocalDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexedSetter(),
                                        new JodaLocalDateTimeToTimestampConverter(JodaHelper.getDateTimeZone(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalDate.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<LocalDate, java.sql.Date>(
                                        new DatePreparedStatementIndexedSetter(),
                                        new JodaLocalDateToDateConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalTime.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<LocalTime, Time>(
                                        new TimePreparedStatementIndexedSetter(),
                                        new JodaLocalTimeToTimeConverter(JodaHelper.getDateTimeZone(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.Instant.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<Instant, Timestamp>(
                                         new TimestampPreparedStatementIndexedSetter(),
                                        new JodaInstantToTimestampConverter());
                    }
                    return null;
                }
            };

    //IFJAVA8_START
    private final Factory javaTimeFieldMapperToSourceFactory =
            new Factory() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                    if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalDateTime.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<java.time.LocalDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexedSetter(),
                                        new JavaLocalDateTimeToTimestampConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalDate.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<java.time.LocalDate, java.sql.Date>(
                                        new DatePreparedStatementIndexedSetter(),
                                        new JavaLocalDateToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalTime.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<java.time.LocalTime, Time>(
                                        new TimePreparedStatementIndexedSetter(),
                                        new JavaLocalTimeToTimeConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.Instant.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<java.time.Instant, Timestamp>(
                                        new TimestampPreparedStatementIndexedSetter(),
                                        new JavaInstantToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.ZonedDateTime.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<ZonedDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexedSetter(),
                                        new JavaZonedDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.OffsetDateTime.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<OffsetDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexedSetter(),
                                        new JavaOffsetDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.OffsetTime.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<OffsetTime, Time>(
                                        new TimePreparedStatementIndexedSetter(),
                                        new JavaOffsetTimeToTimeConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.YearMonth.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<java.time.YearMonth, java.sql.Date>(
                                        new DatePreparedStatementIndexedSetter(),
                                        new JavaYearMonthToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.Year.class)) {
                        return (PrepareStatementIndexedSetter<P>)
                                new ConvertDelegateIndexedSetter<Year, java.sql.Date>(
                                        new DatePreparedStatementIndexedSetter(),
                                        new JavaYearToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    }

                    return null;
                }
            };
    //IFJAVA8_END

    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> arg) {
        PrepareStatementIndexedSetter setter = getIndexedSetter(arg);
        if (setter != null) {
            return new PreparedStatementSetterImpl<P>(arg.getColumnKey().getIndex(), setter);
        } else return null;
    }

    public PrepareStatementIndexedSetter getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> arg) {
        PrepareStatementIndexedSetter setter = null;
        Type propertyType = arg.getPropertyMeta().getPropertyType();
        Factory setterFactory = this.factoryPerClass.get(TypeHelper.toClass(propertyType));
        if (setterFactory != null) {
            setter = setterFactory.indexedSetter(arg);
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
                   setter = new OrdinalEnumPreparedStatementIndexedSetter();
                    break;
                default:
                    setter = new StringEnumPreparedStatementIndexedSetter();
            }
        }

        if (setter == null) {
            setter = jodaTimeFieldMapperToSourceFactory.indexedSetter(arg);
        }

        //IFJAVA8_START
        if (setter == null) {
            setter = javaTimeFieldMapperToSourceFactory.indexedSetter(arg);
        }
        //IFJAVA8_END

        if (setter == null && TypeHelper.isAssignable(SQLData.class, propertyType)) {
            setter = new ObjectPreparedStatementIndexedSetter();
        }
        return setter;
    }

    interface Factory {
        <P> PrepareStatementIndexedSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm);
    }

}
