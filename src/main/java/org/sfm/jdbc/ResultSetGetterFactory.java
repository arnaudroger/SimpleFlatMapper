package org.sfm.jdbc;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.sfm.jdbc.getter.BigDecimalResultSetGetter;
import org.sfm.jdbc.getter.BigIntegerResultSetGetter;
import org.sfm.jdbc.getter.BooleanResultSetGetter;
import org.sfm.jdbc.getter.ByteResultSetGetter;
import org.sfm.jdbc.getter.CharacterResultSetGetter;
import org.sfm.jdbc.getter.DoubleResultSetGetter;
import org.sfm.jdbc.getter.EnumResultSetGetter;
import org.sfm.jdbc.getter.FloatResultSetGetter;
import org.sfm.jdbc.getter.IntResultSetGetter;
import org.sfm.jdbc.getter.LongResultSetGetter;
import org.sfm.jdbc.getter.OrdinalEnumResultSetGetter;
import org.sfm.jdbc.getter.ShortResultSetGetter;
import org.sfm.jdbc.getter.StringEnumResultSetGetter;
import org.sfm.jdbc.getter.StringResultSetGetter;
import org.sfm.jdbc.getter.TimeResultSetGetter;
import org.sfm.jdbc.getter.TimestampResultSetGetter;
import org.sfm.map.GetterFactory;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.Getter;
import org.sfm.reflect.TypeHelper;

public final class ResultSetGetterFactory implements GetterFactory<ResultSet, JdbcColumnKey>{
	public static final int UNDEFINED = -99999;

	@SuppressWarnings("serial")
	private static final Map<Class<?>, GetterFactory<ResultSet, JdbcColumnKey>> factoryPerType = 
		new HashMap<Class<?>, GetterFactory<ResultSet, JdbcColumnKey>>() {{
		put(String.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				return (Getter<ResultSet, P>) new StringResultSetGetter(key.getIndex());
			}
		});
		put(Date.class, new GetterFactory<ResultSet, JdbcColumnKey>() {
			@SuppressWarnings("unchecked")
			@Override
			public <P> Getter<ResultSet, P> newGetter(Type genericType, JdbcColumnKey key) {
				switch (key.getSqlType()) {
				 case JdbcColumnKey.UNDEFINED_TYPE:
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
				case Types.LONGNVARCHAR:
				case Types.LONGVARCHAR:
				case Types.NCHAR:
				case Types.NVARCHAR:
				case Types.VARCHAR:
					return  new StringEnumResultSetGetter<P>(column, type);
				default:
					throw new MapperBuildingException("Incompatible type " + key.getSqlType() + " with enum");
				}
			}
		});	
	}};

	@Override
	public <P> Getter<ResultSet, P> newGetter(Type genericType,
			JdbcColumnKey key) {
		
		Class<?> clazz = TypeHelper.wrap(TypeHelper.toClass(genericType));
		
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
	
	
}
