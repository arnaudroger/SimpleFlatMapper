package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.getter.*;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.impl.*;
import org.sfm.map.impl.fieldmapper.*;
import org.sfm.reflect.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

public final class ResultSetFieldMapperFactory implements FieldMapperFactory<ResultSet, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> {

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
	public <T, P> FieldMapper<ResultSet, T> newFieldMapper(PropertyMapping<T, P, JdbcColumnKey , FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> propertyMapping,  FieldMapperErrorHandler<JdbcColumnKey> errorHandler, MapperBuilderErrorHandler mappingErrorHandler) {

		final Type propertyType = propertyMapping.getPropertyMeta().getType();
		final Setter<T, P> setter = propertyMapping.getPropertyMeta().getSetter();
		final JdbcColumnKey key = propertyMapping.getColumnKey();
		final Class<?> type = TypeHelper.toClass(propertyType);

		Getter<ResultSet, P> getter = (Getter<ResultSet, P>) propertyMapping.getColumnDefinition().getCustomGetter();

		if (getter == null && type.isPrimitive()) {
			return primitiveIndexedFieldMapper(type, setter, key, errorHandler);
		}

		if (getter == null) {
			getter = getterFactory.newGetter(propertyType, key);
		}
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
			mappingErrorHandler.getterNotFound("Could not find getter for " + key + " type " + propertyType);
			return null;
		} else {
			return new FieldMapperImpl<ResultSet, T, P>(getter, setter);
		}
	}

	public Class<?> getTargetTypeFromSqlType(int sqlType) {
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
		case Types.SMALLINT:
			return Short.class;
		case Types.TINYINT:
			return Byte.class;
		case Types.NUMERIC:
			return BigDecimal.class;

		case Types.FLOAT:
			return Float.class;
		case Types.DOUBLE:
			return Double.class;

		case Types.BOOLEAN: 
			return Boolean.class;
		case Types.DATE:
		case Types.TIMESTAMP:
				return Date.class;
		}
		return null;
	}

}
