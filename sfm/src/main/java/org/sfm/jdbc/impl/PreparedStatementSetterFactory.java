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
import java.time.YearMonth;
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
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BooleanPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Boolean.class, factoryPerClass.get(boolean.class));

        factoryPerClass.put(byte.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BytePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Byte.class, factoryPerClass.get(byte.class));

        factoryPerClass.put(char.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new CharacterPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Character.class, factoryPerClass.get(char.class));

        factoryPerClass.put(short.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ShortPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Short.class, factoryPerClass.get(short.class));

        factoryPerClass.put(int.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new IntegerPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Integer.class, factoryPerClass.get(int.class));

        factoryPerClass.put(long.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new LongPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(float.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new FloatPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Float.class, factoryPerClass.get(float.class));

        factoryPerClass.put(double.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new DoublePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Double.class, factoryPerClass.get(double.class));

        factoryPerClass.put(String.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new StringPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Date.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
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
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new TimestampPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(java.sql.Date.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new DatePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Time.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new TimePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Calendar.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
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
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new URLPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Ref.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new RefPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(BigDecimal.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BigDecimalPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Array.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ArrayPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(byte[].class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BytesPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(NClob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new NClobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(RowId.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new RowIdPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Blob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new BlobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Clob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ClobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(InputStream.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new InputStreamPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Reader.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new ReaderPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(SQLXML.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                        return (PreparedStatementIndexSetter<P>) new SQLXMLPreparedStatementIndexSetter();
                    }
                });
    }

    private final Factory jodaTimeFieldMapperToSourceFactory =
            new Factory() {
                @SuppressWarnings("unchecked")
                @Override
                public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                    if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.DateTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<DateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexSetter(),
                                        new JodaDateTimeToTimestampConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalDateTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<LocalDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexSetter(),
                                        new JodaLocalDateTimeToTimestampConverter(JodaHelper.getDateTimeZone(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalDate.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<LocalDate, java.sql.Date>(
                                        new DatePreparedStatementIndexSetter(),
                                        new JodaLocalDateToDateConverter());
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.LocalTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<LocalTime, Time>(
                                        new TimePreparedStatementIndexSetter(),
                                        new JodaLocalTimeToTimeConverter(JodaHelper.getDateTimeZone(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), org.joda.time.Instant.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<Instant, Timestamp>(
                                         new TimestampPreparedStatementIndexSetter(),
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
                public <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm) {
                    if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalDateTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<java.time.LocalDateTime, Timestamp>(
                                        new TimestampPreparedStatementIndexSetter(),
                                        new JavaLocalDateTimeToTimestampConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalDate.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<java.time.LocalDate, java.sql.Date>(
                                        new DatePreparedStatementIndexSetter(),
                                        new JavaLocalDateToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.LocalTime.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<java.time.LocalTime, Time>(
                                        new TimePreparedStatementIndexSetter(),
                                        new JavaLocalTimeToTimeConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
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
                                new ConvertDelegateIndexSetter<YearMonth, java.sql.Date>(
                                        new DatePreparedStatementIndexSetter(),
                                        new JavaYearMonthToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    } else if (TypeHelper.isClass(pm.getPropertyMeta().getPropertyType(), java.time.Year.class)) {
                        return (PreparedStatementIndexSetter<P>)
                                new ConvertDelegateIndexSetter<Year, java.sql.Date>(
                                        new DatePreparedStatementIndexSetter(),
                                        new JavaYearToDateConverter(JavaTimeHelper.getZoneIdOrDefault(pm.getColumnDefinition())));
                    }

                    return null;
                }
            };
    //IFJAVA8_END

    @SuppressWarnings("unchecked")
    @Override
    public <P> Setter<PreparedStatement, P> getSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> arg) {
        PreparedStatementIndexSetter setter = getIndexedSetter(arg);
        if (setter != null) {
            return new PreparedStatementSetterImpl<P>(arg.getColumnKey().getIndex(), setter);
        } else return null;
    }

    public PreparedStatementIndexSetter getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> arg) {
        Type propertyType = arg.getPropertyMeta().getPropertyType();

        PreparedStatementIndexSetter setter = null;
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
                   setter = new OrdinalEnumPreparedStatementIndexSetter();
                    break;
                default:
                    setter = new StringEnumPreparedStatementIndexSetter();
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
            setter = new ObjectPreparedStatementIndexSetter();
        }
        return setter;
    }

    interface Factory {
        <P> PreparedStatementIndexSetter<P> indexedSetter(PropertyMapping<?, ?, JdbcColumnKey, ? extends ColumnDefinition<JdbcColumnKey, ?>> pm);
    }

}
