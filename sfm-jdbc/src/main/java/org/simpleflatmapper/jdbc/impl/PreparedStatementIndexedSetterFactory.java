package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.jdbc.JdbcTypeHelper;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.impl.setter.ArrayPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BigDecimalPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BigIntegerPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BlobPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BooleanPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BytePreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.BytesPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.CharacterPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ClobPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ConvertDelegateIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.DatePreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.DoublePreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.FloatPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.InputStreamPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.IntegerPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.LongPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.NClobPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ObjectPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.OrdinalEnumPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.PreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ReaderPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.RefPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.RowIdPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.SQLXMLPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.ShortPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.StringEnumPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.StringPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.TimePreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.TimestampPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.URLPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.UUIDBinaryPreparedStatementIndexSetter;
import org.simpleflatmapper.jdbc.impl.setter.UUIDStringPreparedStatementIndexSetter;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.setter.ContextualIndexedSetter;
import org.simpleflatmapper.map.setter.ContextualIndexedSetterFactory;
import org.simpleflatmapper.util.TypeHelper;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PreparedStatementIndexedSetterFactory
        implements
        ContextualIndexedSetterFactory<PreparedStatement, PropertyMapping<?, ?, JdbcColumnKey>>
        {
    public static final PreparedStatementIndexedSetterFactory INSTANCE = new PreparedStatementIndexedSetterFactory();

    private final Map<Class<?>, Factory> factoryPerClass = new HashMap<Class<?>, Factory>();

    private PreparedStatementIndexedSetterFactory() {
        factoryPerClass.put(boolean.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new BooleanPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Boolean.class, factoryPerClass.get(boolean.class));

        factoryPerClass.put(byte.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new BytePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Byte.class, factoryPerClass.get(byte.class));

        factoryPerClass.put(char.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new CharacterPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Character.class, factoryPerClass.get(char.class));

        factoryPerClass.put(short.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new ShortPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Short.class, factoryPerClass.get(short.class));

        factoryPerClass.put(int.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new IntegerPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Integer.class, factoryPerClass.get(int.class));

        factoryPerClass.put(long.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new LongPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Long.class, factoryPerClass.get(long.class));

        factoryPerClass.put(float.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new FloatPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Float.class, factoryPerClass.get(float.class));

        factoryPerClass.put(double.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new DoublePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Double.class, factoryPerClass.get(double.class));

        factoryPerClass.put(String.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new StringPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Timestamp.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new TimestampPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Date.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new DatePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Time.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new TimePreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(URL.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new URLPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Ref.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new RefPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(BigDecimal.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new BigDecimalPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(BigInteger.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new BigIntegerPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Array.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new ArrayPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(byte[].class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new BytesPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(NClob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new NClobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(RowId.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new RowIdPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Blob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new BlobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Clob.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new ClobPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(InputStream.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new InputStreamPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(Reader.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new ReaderPreparedStatementIndexSetter();
                    }
                });
        factoryPerClass.put(SQLXML.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        return (PreparedStatementIndexSetter<P>) new SQLXMLPreparedStatementIndexSetter();
                    }
                });

        factoryPerClass.put(UUID.class,
                new Factory() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                        switch (key.getSqlType(properties)) {
                            case Types.BINARY:
                            case Types.VARBINARY:
                            case Types.LONGVARBINARY:
                                return (PreparedStatementIndexSetter<P>) new UUIDBinaryPreparedStatementIndexSetter();
                            case Types.OTHER:
                                // asssume it's a postgres uuid
                                return (PreparedStatementIndexSetter<P>) new ObjectPreparedStatementIndexSetter();
                        }
                        return (PreparedStatementIndexSetter<P>) new UUIDStringPreparedStatementIndexSetter();
                    }
                });

        // see http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html
        //IFJAVA8_START
        factoryPerClass.put(java.time.OffsetTime.class, new Factory() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                if (key.getSqlType(properties) == Types.TIME_WITH_TIMEZONE) {
                    return (PreparedStatementIndexSetter<P>) new ObjectPreparedStatementIndexSetter();
                }
                return null;
            }
        });
        factoryPerClass.put(java.time.OffsetDateTime.class, new Factory() {
            @SuppressWarnings("unchecked")
            @Override
            public <P> PreparedStatementIndexSetter<P> getIndexedSetter(JdbcColumnKey key, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
                if (key.getSqlType(properties) == Types.TIMESTAMP_WITH_TIMEZONE) {
                    return (PreparedStatementIndexSetter<P>) new ObjectPreparedStatementIndexSetter();
                }
                return null;
            }
        });
        //IFJAVA8_END
    }


    @Override
    public  <T> ContextualIndexedSetter<PreparedStatement, T> getIndexedSetter(PropertyMapping<?, ?, JdbcColumnKey> arg, ContextFactoryBuilder contextFactoryBuilder, Object... properties) {
        Type propertyType = arg.getPropertyMeta().getPropertyType();
        ContextualIndexedSetter<PreparedStatement, T> setter = getIndexedSetter(propertyType, arg, contextFactoryBuilder);

        if (setter == null) {
            Class<?> iclass = JdbcTypeHelper.toJavaType(arg.getColumnKey().getSqlType(properties), propertyType);
            setter = getSetterWithConvertion(TypeHelper.<T>toClass(propertyType), iclass,contextFactoryBuilder, arg);
        }

        return setter;
    }


    @SuppressWarnings("unchecked")
    private <P, I> ContextualIndexedSetter<PreparedStatement, P> getSetterWithConvertion(Class<P> pclazz, Class<I> iclass, ContextFactoryBuilder contextFactoryBuilder, PropertyMapping<?, ?, JdbcColumnKey> pm) {
        ContextualConverter<? super P, ? extends I> converter = ConverterService.getInstance().findConverter(pclazz, iclass, contextFactoryBuilder, pm.getColumnDefinition().properties());

        if (converter != null) {
            ContextualIndexedSetter<PreparedStatement, I> indexedSetter = getIndexedSetter(iclass, pm, contextFactoryBuilder);
            if (indexedSetter != null) {
                return new ConvertDelegateIndexSetter<P, I>(indexedSetter, converter);
            }
        }


        return null;
    }

    @SuppressWarnings("unchecked")
    protected  <T> ContextualIndexedSetter<PreparedStatement, T> getIndexedSetter(Type propertyType, PropertyMapping<?, ?, JdbcColumnKey> arg, ContextFactoryBuilder contextFactoryBuilder) {
        ContextualIndexedSetter<PreparedStatement, T> setter = null;

        if (TypeHelper.isEnum(propertyType)) {
            switch (arg.getColumnKey().getSqlType(arg.getColumnDefinition().properties())) {
                case Types.NUMERIC:
                case Types.BIGINT:
                case Types.INTEGER:
                case Types.DECIMAL:
                case Types.DOUBLE:
                case Types.FLOAT:
                case Types.SMALLINT:
                case Types.REAL:
                case Types.TINYINT:
                   setter = (ContextualIndexedSetter<PreparedStatement, T>) new OrdinalEnumPreparedStatementIndexSetter();
                    break;
                default:
                    setter = (ContextualIndexedSetter<PreparedStatement, T>) new StringEnumPreparedStatementIndexSetter();
            }
        }

        if (setter == null) {
            Factory setterFactory = this.factoryPerClass.get(TypeHelper.toClass(propertyType));
            if (setterFactory != null) {
                setter = setterFactory.<T>getIndexedSetter(arg.getColumnKey(), contextFactoryBuilder, arg.getColumnDefinition().properties());
            }
        }

        if (setter == null && TypeHelper.isAssignable(SQLData.class, propertyType)) {
            setter = (ContextualIndexedSetter<PreparedStatement, T>) new ObjectPreparedStatementIndexSetter();
        }

        return setter;
    }

    public interface Factory
            extends ContextualIndexedSetterFactory<PreparedStatement, JdbcColumnKey> {
    }

}
