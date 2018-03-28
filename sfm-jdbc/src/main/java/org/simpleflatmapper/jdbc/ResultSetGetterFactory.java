package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.jdbc.impl.getter.BigDecimalFromStringResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.BigIntegerFromStringResultSetGetter;
import org.simpleflatmapper.reflect.getter.BytesUUIDGetter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.getter.GetterFactoryRegistry;
import org.simpleflatmapper.reflect.getter.StringUUIDGetter;
import org.simpleflatmapper.reflect.getter.UUIDUnspecifiedTypeGetter;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.reflect.Getter;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.*;
import java.util.Date;
import java.util.UUID;

import org.simpleflatmapper.jdbc.impl.getter.BigDecimalResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.BigIntegerResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.BlobResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.BooleanResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.ByteArrayResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.ByteResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.CalendarResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.CharacterResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.ClobResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.DateResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.DoubleResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.FloatResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.InputStreamResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.IntResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.LongResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.NClobResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.NReaderResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.NStringResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.ObjectResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.ReaderResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.RefResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.RowIdResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.SQLXMLResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.ShortResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.SqlArrayResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.StringResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.TimeResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.TimestampResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.UndefinedDateResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.UrlFromStringResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.UrlResultSetGetter;
import org.simpleflatmapper.util.TypeHelper;

public final class ResultSetGetterFactory implements GetterFactory<ResultSet, JdbcColumnKey> {
	public static final ResultSetGetterFactory INSTANCE = new ResultSetGetterFactory();

	private ResultSetGetterFactory() {
	}

	public static final GetterFactory<ResultSet, JdbcColumnKey> DATE_GETTER_FACTORY = new GetterFactory<ResultSet, JdbcColumnKey>() {
		@SuppressWarnings("unchecked")
		@Override
		public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
			switch (key.getSqlType(properties)) {
				case JdbcColumnKey.UNDEFINED_TYPE:
					return (Getter<ResultSet, P>) new UndefinedDateResultSetGetter(key.getIndex());
				case Types.TIMESTAMP:
					return (Getter<ResultSet, P>) new TimestampResultSetGetter(key.getIndex());
				case Types.DATE:
					return (Getter<ResultSet, P>) new DateResultSetGetter(key.getIndex());
				case Types.TIME:
					return (Getter<ResultSet, P>) new TimeResultSetGetter(key.getIndex());
				default:
					return null;
			}
		}
	};

	public static final class StringResultSetGetterFactory implements
			GetterFactory<ResultSet, JdbcColumnKey> {
		@SuppressWarnings("unchecked")
		@Override
		public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
			switch(key.getSqlType(properties) ) {
			case Types.NCHAR:
			case Types.NVARCHAR:
			case Types.LONGNVARCHAR:
			case Types.NCLOB:
				return (Getter<ResultSet, P>) new NStringResultSetGetter(key.getIndex());
			default:
				return (Getter<ResultSet, P>) new StringResultSetGetter(key.getIndex());
			}
		}
	}

	private static final GetterFactoryRegistry<ResultSet, JdbcColumnKey> factoryRegistry =
		new GetterFactoryRegistry<ResultSet, JdbcColumnKey>();

	static {
		factoryRegistry.put(String.class, new StringResultSetGetterFactory());
		factoryRegistry.put(Date.class, DATE_GETTER_FACTORY);

		// see http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html
		//IFJAVA8_START
		factoryRegistry.put(java.time.OffsetTime.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				if (key.getSqlType(properties) == Types.TIME_WITH_TIMEZONE) {
					return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());

				}
				return null;
			}
		});
		factoryRegistry.put(java.time.OffsetDateTime.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				if (key.getSqlType(properties) == Types.TIMESTAMP_WITH_TIMEZONE) {
					return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());

				}
				return null;
			}
		});
		//IFJAVA8_END

		factoryRegistry.put(java.util.Calendar.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				Getter<ResultSet, Date> dateGetter = DATE_GETTER_FACTORY.newGetter(Date.class, key, properties);
				if (dateGetter != null) {
					return (Getter<ResultSet, P>) new CalendarResultSetGetter(dateGetter);
				}
				return null;
			}
		});

		factoryRegistry.put(java.sql.Date.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new DateResultSetGetter(key.getIndex());
			}
		});

		factoryRegistry.put(java.sql.Timestamp.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new TimestampResultSetGetter(key.getIndex());
			}
		});

		factoryRegistry.put(java.sql.Time.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new TimeResultSetGetter(key.getIndex());
			}
		});

		factoryRegistry.put(Boolean.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new BooleanResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Byte.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ByteResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Character.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new CharacterResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Short.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ShortResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Integer.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new IntResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Long.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new LongResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Float.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new FloatResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Double.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new DoubleResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(BigInteger.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				switch (key.getSqlType(properties)) {
					case Types.LONGVARCHAR:
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.CLOB:
						return (Getter<ResultSet, P>) new BigIntegerFromStringResultSetGetter(new StringResultSetGetter(key.getIndex()));
					case Types.LONGNVARCHAR:
					case Types.NCHAR:
					case Types.NVARCHAR:
					case Types.NCLOB:
						return (Getter<ResultSet, P>) new BigIntegerFromStringResultSetGetter(new NStringResultSetGetter(key.getIndex()));
					default:
						return (Getter<ResultSet, P>) new BigIntegerResultSetGetter(key.getIndex());
				}
			}
		});
		factoryRegistry.put(BigDecimal.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				switch (key.getSqlType(properties)) {
					case Types.LONGVARCHAR:
					case Types.CHAR:
					case Types.VARCHAR:
					case Types.CLOB:
						return (Getter<ResultSet, P>) new BigDecimalFromStringResultSetGetter(new StringResultSetGetter(key.getIndex()));
					case Types.LONGNVARCHAR:
					case Types.NCHAR:
					case Types.NVARCHAR:
					case Types.NCLOB:
						return (Getter<ResultSet, P>) new BigDecimalFromStringResultSetGetter(new NStringResultSetGetter(key.getIndex()));
					default:
						return (Getter<ResultSet, P>) new BigDecimalResultSetGetter(key.getIndex());
				}
			}
		});

		factoryRegistry.put(URL.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				if (key.getSqlType(properties) == Types.DATALINK) {
					return (Getter<ResultSet, P>) new UrlResultSetGetter(key.getIndex());
				} else {
					return (Getter<ResultSet, P>) new UrlFromStringResultSetGetter(key.getIndex());
				}
			}
		});

		factoryRegistry.put(byte[].class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ByteArrayResultSetGetter(key.getIndex());
			}
		});

		factoryRegistry.put(InputStream.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new InputStreamResultSetGetter(key.getIndex());
			}
		});

		factoryRegistry.put(Blob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new BlobResultSetGetter(key.getIndex());
			}
		});

		factoryRegistry.put(Reader.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				switch (key.getSqlType(properties)) {
					case Types.LONGNVARCHAR:
					case Types.NCHAR:
					case Types.NVARCHAR:
					case Types.NCLOB:
						return (Getter<ResultSet, P>) new NReaderResultSetGetter(key.getIndex());
					default:
						return (Getter<ResultSet, P>) new ReaderResultSetGetter(key.getIndex());
				}
			}
		});

		factoryRegistry.put(Clob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ClobResultSetGetter(key.getIndex());
			}
		});

		factoryRegistry.put(NClob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new NClobResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Ref.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new RefResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(RowId.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new RowIdResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(SQLXML.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new SQLXMLResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(Array.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new SqlArrayResultSetGetter(key.getIndex());
			}
		});
		factoryRegistry.put(UUID.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type target, JdbcColumnKey key, Object... properties) {
				switch (key.getSqlType(properties)) {
					case JdbcColumnKey.UNDEFINED_TYPE:
						return (Getter<ResultSet, P>)
								new UUIDUnspecifiedTypeGetter<ResultSet>(new ObjectResultSetGetter(key.getIndex()));
					case Types.CHAR:
					case Types.LONGVARCHAR:
					case Types.VARCHAR:
					case Types.CLOB:
						return (Getter<ResultSet, P>) new StringUUIDGetter<ResultSet>(new StringResultSetGetter(key.getIndex()));
					case Types.LONGNVARCHAR:
					case Types.NCHAR:
					case Types.NVARCHAR:
					case Types.NCLOB:
						return (Getter<ResultSet, P>) new StringUUIDGetter<ResultSet>(new NStringResultSetGetter(key.getIndex()));
					case Types.BINARY:
					case Types.LONGVARBINARY:
					case Types.VARBINARY:
						return (Getter<ResultSet, P>) new BytesUUIDGetter<ResultSet>(new ByteArrayResultSetGetter(key.getIndex()));
					case Types.OTHER:
						// assume it's a UUID postgres
						return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());
					default:
						throw new MapperBuildingException("Incompatible type " + key.getSqlType(properties) + " with UUID");
				}
			}
		});
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> Getter<ResultSet, P> newGetter(Type genericType,
											  JdbcColumnKey key, Object... properties) {
		Class<?> clazz = TypeHelper.wrap(TypeHelper.toClass(genericType));

		if (Object.class.equals(clazz)) {
			return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());
		}

		GetterFactory<ResultSet, JdbcColumnKey> getterFactory =
				factoryRegistry.findFactoryFor(clazz);

		Getter<ResultSet, P> getter = null;
		if (getterFactory != null) {
			getter = (Getter<ResultSet, P>) getterFactory.newGetter(genericType, key, properties);
		}

		if (getter == null) {
			if (SQLData.class.isAssignableFrom(clazz) || key.getSqlType(properties) == Types.JAVA_OBJECT) {
				return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());
			}
		}

		return getter;
	}
}
