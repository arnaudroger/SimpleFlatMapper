package org.sfm.jdbc.impl.getter;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLXML;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.impl.GetterFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

public final class ResultSetGetterFactory implements GetterFactory<ResultSet, JdbcColumnKey>{
	public static final class StringResultSetGetterFactory implements
			GetterFactory<ResultSet, JdbcColumnKey> {
		@SuppressWarnings("unchecked")
		@Override
		public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
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

	public static final int UNDEFINED = -99999;

	@SuppressWarnings("serial")
	private static final Map<Class<?>, GetterFactory<ResultSet, JdbcColumnKey>> factoryPerType = 
		new HashMap<Class<?>, GetterFactory<ResultSet, JdbcColumnKey>>() {{
		put(String.class, new StringResultSetGetterFactory());
		put(Date.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
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
		});
		put(Boolean.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new BooleanResultSetGetter(key.getIndex());
			}
		});
		put(Byte.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new ByteResultSetGetter(key.getIndex());
			}
		});
		put(Character.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new CharacterResultSetGetter(key.getIndex());
			}
		});
		put(Short.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new ShortResultSetGetter(key.getIndex());
			}
		});
		put(Integer.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new IntResultSetGetter(key.getIndex());
			}
		});
		put(Long.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new LongResultSetGetter(key.getIndex());
			}
		});
		put(Float.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new FloatResultSetGetter(key.getIndex());
			}
		});
		put(Double.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new DoubleResultSetGetter(key.getIndex());
			}
		});
		put(BigInteger.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new BigIntegerResultSetGetter(key.getIndex());
			}
		});
		put(BigDecimal.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new BigDecimalResultSetGetter(key.getIndex());
			}
		});
		
		put(Enum.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type type, JdbcColumnKey key) {
				@SuppressWarnings("rawtypes")
				Class<? extends Enum> enumClass = TypeHelper.toClass(type); 
				return (Getter<ResultSet, P>) newEnumGetter(enumClass, key);
			}
			
			private <P extends Enum<P>> Getter<ResultSet, P> newEnumGetter(Class<P> type, JdbcColumnKey key) {
				int column = key.getIndex();
				switch(key.getSqlType()) {
				case JdbcColumnKey.UNDEFINED_TYPE: 
					return new EnumResultSetGetter<P>(column, type);
				case Types.BIGINT:
				case Types.INTEGER:
				case Types.NUMERIC:
				case Types.SMALLINT:
				case Types.TINYINT:
					return  new OrdinalEnumResultSetGetter<P>(column, type);
				case Types.CHAR:
				case Types.LONGVARCHAR:
				case Types.VARCHAR:
				case Types.CLOB:
					return  new StringEnumResultSetGetter<P>(new StringResultSetGetter(column), type);
				case Types.LONGNVARCHAR:
				case Types.NCHAR:
				case Types.NVARCHAR:
				case Types.NCLOB:
					return  new StringEnumResultSetGetter<P>(new NStringResultSetGetter(column), type);
				default:
					throw new MapperBuildingException("Incompatible type " + key.getSqlType() + " with enum");
				}
			}
		});	
		
		put(URL.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				if (key.getSqlType() == Types.DATALINK) {
					return (Getter<ResultSet, P>) new UrlResultSetGetter(key.getIndex());
				} else {
					return (Getter<ResultSet, P>) new UrlFromStringResultSetGetter(key.getIndex());
				}
			}
		});
		
		put(byte[].class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new ByteArrayResultSetGetter(key.getIndex());
			}
		});
		
		put(InputStream.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new InputStreamResultSetGetter(key.getIndex());
			}
		});
		
		put(Blob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new BlobResultSetGetter(key.getIndex());
			}
		});
		
		put(Reader.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				switch(key.getSqlType()) {
				case Types.LONGNVARCHAR:
				case Types.NCHAR:
				case Types.NVARCHAR:
				case Types.NCLOB:
					return (Getter<ResultSet, P>) new ReaderResultSetGetter(key.getIndex());
				default:
					return (Getter<ResultSet, P>) new NReaderResultSetGetter(key.getIndex());
				}
			}
		});
		
		put(Clob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new ClobResultSetGetter(key.getIndex());
			}
		});
		
		put(NClob.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new NClobResultSetGetter(key.getIndex());
			}
		});
		put(Ref.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new NClobResultSetGetter(key.getIndex());
			}
		});
		put(Ref.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new RefResultSetGetter(key.getIndex());
			}
		});
		put(RowId.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new RowIdResultSetGetter(key.getIndex());
			}
		});
		put(SQLXML.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new SQLXMLResultSetGetter(key.getIndex());
			}
		});
		put(Array.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new SqlArrayResultSetGetter(key.getIndex());
			}
		});
	}};

	@SuppressWarnings("unchecked")
	@Override
	public <P> Getter<ResultSet, P> newGetter(Type genericType,
			JdbcColumnKey key) {
		
		Class<?> clazz = TypeHelper.wrap(TypeHelper.toClass(genericType));
		
		if (clazz.isArray() && key.getSqlType() == Types.ARRAY) {
			Class<?> elementType = clazz.getComponentType();
			return (Getter<ResultSet, P>) newArrayGetter(elementType, key);
		}
		
		GetterFactory<ResultSet, JdbcColumnKey> getterFactory = factoryPerType.get(clazz);
		
		if (getterFactory == null) {
			for(Entry<Class<?>, GetterFactory<ResultSet, JdbcColumnKey>> e : factoryPerType.entrySet()) {
				if (e.getKey().isAssignableFrom(clazz)) {
					getterFactory = e.getValue();
					break;
				}
			}
		}
		
		Getter<ResultSet, P> getter = null;
		if (getterFactory != null) {
			getter = getterFactory.newGetter(genericType, key);
		}
		return getter;
	}

	private <E> Getter<ResultSet, E[]> newArrayGetter(Class<E> elementType, JdbcColumnKey key) {
		Getter<ResultSet, E> elementGetter = newGetter(elementType, new JdbcColumnKey("elt", 2));
		
		if (elementGetter != null) {
			return new ArrayResultSetGetter<E>(key.getIndex(), elementType, elementGetter);
		}
		
		return null;
	}
	
	
}
