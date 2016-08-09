package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.jdbc.impl.JDBCTypeHelper;
import org.simpleflatmapper.reflect.getter.GetterWithConverter;
import org.simpleflatmapper.reflect.getter.BytesUUIDGetter;
import org.simpleflatmapper.reflect.getter.EnumUnspecifiedTypeGetter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.getter.OrdinalEnumGetter;
import org.simpleflatmapper.reflect.getter.StringEnumGetter;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.simpleflatmapper.jdbc.impl.getter.ArrayResultSetGetter;
import org.simpleflatmapper.jdbc.impl.getter.ArrayToListResultSetGetter;
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

	public static final GetterFactory<ResultSet, JdbcColumnKey> DATE_GETTER_FACTORY = new GetterFactory<ResultSet, JdbcColumnKey>() {
		@SuppressWarnings("unchecked")
		@Override
		public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
			switch (key.getSqlType()) {
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

	private final ConverterService converterService = ConverterService.getInstance();

	public static final class StringResultSetGetterFactory implements
			GetterFactory<ResultSet, JdbcColumnKey> {
		@SuppressWarnings("unchecked")
		@Override
		public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
			switch(key.getSqlType() ) {
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

	public static final GetterFactory<ResultSet, JdbcColumnKey> ENUM_GETTER_FACTORY = new GetterFactory<ResultSet, JdbcColumnKey>() {
		@SuppressWarnings("unchecked")
		@Override
		public <P> Getter<ResultSet, P> newGetter(Type type, JdbcColumnKey key, Object... properties) {
			@SuppressWarnings("rawtypes")
			Class<? extends Enum> enumClass = TypeHelper.toClass(type);
			return (Getter<ResultSet, P>) newEnumGetter(enumClass, key);
		}

		private <P extends Enum<P>> Getter<ResultSet, P> newEnumGetter(Class<P> type, JdbcColumnKey key) {
			int column = key.getIndex();
			switch (key.getSqlType()) {
				case JdbcColumnKey.UNDEFINED_TYPE:
					return new EnumUnspecifiedTypeGetter<ResultSet, P>(new ObjectResultSetGetter(column), type);
				case Types.BIGINT:
				case Types.INTEGER:
				case Types.NUMERIC:
				case Types.SMALLINT:
				case Types.TINYINT:
					return new OrdinalEnumGetter<ResultSet, P>(new IntResultSetGetter(column), type);
				case Types.CHAR:
				case Types.LONGVARCHAR:
				case Types.VARCHAR:
				case Types.CLOB:
					return new StringEnumGetter<ResultSet, P>(new StringResultSetGetter(column), type);
				case Types.LONGNVARCHAR:
				case Types.NCHAR:
				case Types.NVARCHAR:
				case Types.NCLOB:
					return new StringEnumGetter<ResultSet, P>(new NStringResultSetGetter(column), type);
				default:
					throw new MapperBuildingException("Incompatible type " + key.getSqlType() + " with enum");
			}
		}
	};

	private static final Map<Class<?>, GetterFactory<ResultSet, JdbcColumnKey>> factoryPerType =
		new HashMap<Class<?>, GetterFactory<ResultSet, JdbcColumnKey>>();

	static {
		factoryPerType.put(String.class, new StringResultSetGetterFactory());
		factoryPerType.put(Date.class, DATE_GETTER_FACTORY);

		// see http://www.oracle.com/technetwork/articles/java/jf14-date-time-2125367.html
		//JAVA8_START
		factoryPerType.put(java.time.OffsetTime.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				if (key.getSqlType() == Types.TIME_WITH_TIMEZONE) {
					return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());

				}
				return null;
			}
		});
		factoryPerType.put(java.time.OffsetDateTime.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				if (key.getSqlType() == Types.TIMESTAMP_WITH_TIMEZONE) {
					return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());

				}
				return null;
			}
		});
		//JAVA8_END

		factoryPerType.put(java.util.Calendar.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
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

		factoryPerType.put(java.sql.Date.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new DateResultSetGetter(key.getIndex());
			}
		});

		factoryPerType.put(java.sql.Timestamp.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new TimestampResultSetGetter(key.getIndex());
			}
		});

		factoryPerType.put(java.sql.Time.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new TimeResultSetGetter(key.getIndex());
			}
		});

		factoryPerType.put(Boolean.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new BooleanResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Byte.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ByteResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Character.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new CharacterResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Short.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ShortResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Integer.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new IntResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Long.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new LongResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Float.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new FloatResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Double.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new DoubleResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(BigInteger.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new BigIntegerResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(BigDecimal.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new BigDecimalResultSetGetter(key.getIndex());
			}
		});

		factoryPerType.put(URL.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				if (key.getSqlType() == Types.DATALINK) {
					return (Getter<ResultSet, P>) new UrlResultSetGetter(key.getIndex());
				} else {
					return (Getter<ResultSet, P>) new UrlFromStringResultSetGetter(key.getIndex());
				}
			}
		});

		factoryPerType.put(byte[].class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ByteArrayResultSetGetter(key.getIndex());
			}
		});

		factoryPerType.put(InputStream.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new InputStreamResultSetGetter(key.getIndex());
			}
		});

		factoryPerType.put(Blob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new BlobResultSetGetter(key.getIndex());
			}
		});

		factoryPerType.put(Reader.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				switch (key.getSqlType()) {
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

		factoryPerType.put(Clob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ClobResultSetGetter(key.getIndex());
			}
		});

		factoryPerType.put(NClob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new NClobResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Ref.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new RefResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(RowId.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new RowIdResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(SQLXML.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new SQLXMLResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(Array.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new SqlArrayResultSetGetter(key.getIndex());
			}
		});
		factoryPerType.put(UUID.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type target, JdbcColumnKey key, Object... properties) {
				switch (key.getSqlType()) {
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
					default:
						throw new MapperBuildingException("Incompatible type " + key.getSqlType() + " with enum");
				}
			}
		});

		GetterFactory<ResultSet, JdbcColumnKey> objectGetterFactory = new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type target, JdbcColumnKey key, Object... properties) {
				return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());
			}
		};
	}

	@SuppressWarnings("unchecked")
	@Override
	public <P> Getter<ResultSet, P> newGetter(Type genericType,
											  JdbcColumnKey key, Object... properties) {

		Getter<ResultSet, P> getter = _newGetter(genericType, key, properties);

		if (getter != null) return getter;
		;

		// convertion fall back
		Class<?> sqlDataType = JDBCTypeHelper.toJavaType(key.getSqlType(), genericType);
		getter = lookForGetterWithConvertion(sqlDataType, (Class<P>) TypeHelper.toClass(genericType), key, properties);

		return getter;
	}

	private <P> Getter<ResultSet, P> _newGetter(Type genericType, JdbcColumnKey key, Object[] properties) {
		Class<?> clazz = TypeHelper.wrap(TypeHelper.toClass(genericType));

		if (Object.class.equals(clazz)) {
			return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());
		}

		if (key.getSqlType() == Types.ARRAY) {
			if (clazz.isArray()) {
				Class<?> elementType = clazz.getComponentType();
				return (Getter<ResultSet, P>) newArrayGetter(elementType, key, properties);
			} else if (TypeHelper.isAssignable(List.class, genericType)) {
				Type elementType = TypeHelper.getComponentTypeOfListOrArray(genericType);
				return (Getter<ResultSet, P>) newArrayListGetter(elementType, key, properties);
			}
		}

		GetterFactory<ResultSet, JdbcColumnKey> getterFactory;

		if (clazz.isEnum()) {
			getterFactory = ENUM_GETTER_FACTORY;
		} else {
			getterFactory = factoryPerType.get(clazz);
		}


		if (getterFactory == null) {
			for(Entry<Class<?>, GetterFactory<ResultSet, JdbcColumnKey>> e : factoryPerType.entrySet()) {
				if (clazz.isAssignableFrom(e.getKey())) {
					getterFactory = e.getValue();
					break;
				}
			}
		}

		Getter<ResultSet, P> getter = null;
		if (getterFactory != null) {
			getter = (Getter<ResultSet, P>) getterFactory.newGetter(genericType, key, properties);
		}

		if (getter == null) {
			if (SQLData.class.isAssignableFrom(clazz) || key.getSqlType() == Types.JAVA_OBJECT) {
				return (Getter<ResultSet, P>) new ObjectResultSetGetter(key.getIndex());
			}
		}

		return getter;
	}

	private <P, J> Getter<ResultSet, P> lookForGetterWithConvertion(Class<J> sqlDataType, Class<P> propertyType, JdbcColumnKey key, Object[] properties) {
		Converter<? super J, ? extends P> converter = converterService.findConverter(sqlDataType, propertyType, properties);

		if (converter != null) {
			Getter<ResultSet, J> getter = _newGetter(sqlDataType, key, properties);

			return new GetterWithConverter<ResultSet, J, P>(converter, getter);
		}
		return null;
	}

	private <E> Getter<ResultSet, E[]> newArrayGetter(Class<E> elementType, JdbcColumnKey key, Object... properties) {
		Getter<ResultSet, E> elementGetter = newGetter(elementType, new JdbcColumnKey("elt", 2), properties);
		
		if (elementGetter != null) {
			return new ArrayResultSetGetter<E>(key.getIndex(), elementType, elementGetter);
		}
		
		return null;
	}
	
	private <E> Getter<ResultSet, List<E>> newArrayListGetter(Type elementType, JdbcColumnKey key, Object... properties) {
		Getter<ResultSet, E> elementGetter = newGetter(elementType, new JdbcColumnKey("elt", 2), properties);
		
		if (elementGetter != null) {
			return new ArrayToListResultSetGetter<E>(key.getIndex(), elementGetter);
		}
		
		return null;
	}
}
