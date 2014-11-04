package org.sfm.jdbc.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.getter.BooleanResultSetGetter;
import org.sfm.jdbc.impl.getter.ByteResultSetGetter;
import org.sfm.jdbc.impl.getter.CharacterResultSetGetter;
import org.sfm.jdbc.impl.getter.DoubleResultSetGetter;
import org.sfm.jdbc.impl.getter.FloatResultSetGetter;
import org.sfm.jdbc.impl.getter.IntResultSetGetter;
import org.sfm.jdbc.impl.getter.LongResultSetGetter;
import org.sfm.jdbc.impl.getter.ShortResultSetGetter;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.FieldMapperFactory;
import org.sfm.map.impl.GetterFactory;
import org.sfm.map.impl.fieldmapper.BooleanFieldMapper;
import org.sfm.map.impl.fieldmapper.ByteFieldMapper;
import org.sfm.map.impl.fieldmapper.CharacterFieldMapper;
import org.sfm.map.impl.fieldmapper.DoubleFieldMapper;
import org.sfm.map.impl.fieldmapper.FieldMapperImpl;
import org.sfm.map.impl.fieldmapper.FloatFieldMapper;
import org.sfm.map.impl.fieldmapper.IntFieldMapper;
import org.sfm.map.impl.fieldmapper.LongFieldMapper;
import org.sfm.map.impl.fieldmapper.ShortFieldMapper;
import org.sfm.reflect.ConstructorOnGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;

public final class ResultSetFieldMapperFactory implements FieldMapperFactory<ResultSet, JdbcColumnKey> {

	private final GetterFactory<ResultSet, JdbcColumnKey>  getterFactory;

	public ResultSetFieldMapperFactory(GetterFactory<ResultSet, JdbcColumnKey> getterFactory) {
		this.getterFactory = getterFactory;
	}


	private <T> FieldMapper<ResultSet, T> primitiveIndexedFieldMapper(final Class<?> type, final Setter<T, ?> setter, final JdbcColumnKey key, final FieldMapperErrorHandler<JdbcColumnKey> errorHandler) {
		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(
					new BooleanResultSetGetter(key.getIndex()),
					SetterFactory.toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(
					new IntResultSetGetter(key.getIndex()),
					SetterFactory.toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(
					new LongResultSetGetter(key.getIndex()),
					SetterFactory.toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(
					new FloatResultSetGetter(key.getIndex()),
					SetterFactory.toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(
					new DoubleResultSetGetter(key.getIndex()),
					SetterFactory.toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(
					new ByteResultSetGetter(key.getIndex()),
					SetterFactory.toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(
					new CharacterResultSetGetter(key.getIndex()),
					SetterFactory.toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(
					new ShortResultSetGetter(key.getIndex()),
					SetterFactory.toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}

	@Override
	public <T, P> FieldMapper<ResultSet, T> newFieldMapper(Type propretyType, Setter<T, P> setter,
			JdbcColumnKey key, FieldMapperErrorHandler<JdbcColumnKey> errorHandler, MapperBuilderErrorHandler mappingErrorHandler) {
		final Class<?> type = TypeHelper.toClass(propretyType);

		if (type.isPrimitive()) {
			return primitiveIndexedFieldMapper(type, setter, key, errorHandler);
		}
		
		Getter<ResultSet, P> getter = getterFactory.newGetter(propretyType, key);
		if (getter == null) {
			// check if has a one arg construct
			final Constructor<?>[] constructors = type.getConstructors();
			if (constructors != null && constructors.length == 1 && constructors[0].getParameterTypes().length == 1) {
				@SuppressWarnings("unchecked")
				final Constructor<P> constructor = (Constructor<P>) constructors[0];
				getter = getterFactory.newGetter(constructor.getParameterTypes()[0], key);
				
				if (getter != null) {
					getter = new ConstructorOnGetter<ResultSet, P>(constructor, getter);
				}
			} else if (key.getSqlType() != JdbcColumnKey.UNDEFINED_TYPE) {
				Class<?> targetType = getTargetTypeFromSqlType(key.getSqlType());
				if (targetType != null) {
					try {
						@SuppressWarnings("unchecked")
						Constructor<P> constructor = (Constructor<P>) type.getConstructor(targetType);
						getter = getterFactory.newGetter(targetType, key);
						
						if (getter != null) {
							getter = new ConstructorOnGetter<ResultSet, P>(constructor, getter);
						}
					} catch (Exception e) {
						// ignore 
					}
				}
			}
		}
		if (getter == null) {
			mappingErrorHandler.getterNotFound("Could not find getter for " + key + " type " + propretyType);
			return null;
		} else {
			return new FieldMapperImpl<ResultSet, T, P>(getter, setter);
		}
	}

	private Class<?> getTargetTypeFromSqlType(int sqlType) {
		switch (sqlType) {
		case Types.LONGNVARCHAR:
		case Types.LONGVARCHAR:
		case Types.CHAR:
		case Types.CLOB:
		case Types.NCHAR:
		case Types.NCLOB:
		case Types.NVARCHAR:
		case Types.VARCHAR:
			return String.class;
			
		case Types.BIGINT:
			return Long.class;
		case Types.INTEGER:
			return Integer.class;
			
		case Types.FLOAT:
			return Float.class;
		case Types.DOUBLE:
			return Double.class;
		case Types.BOOLEAN: 
			return Boolean.class;
		case Types.DATE: 
			return Date.class;
		}
		return null;
	}

}
