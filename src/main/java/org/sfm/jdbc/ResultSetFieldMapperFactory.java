package org.sfm.jdbc;

import java.lang.reflect.Constructor;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Date;

import org.sfm.jdbc.getter.BooleanIndexedResultSetGetter;
import org.sfm.jdbc.getter.BooleanNamedResultSetGetter;
import org.sfm.jdbc.getter.ByteIndexedResultSetGetter;
import org.sfm.jdbc.getter.ByteNamedResultSetGetter;
import org.sfm.jdbc.getter.CharacterIndexedResultSetGetter;
import org.sfm.jdbc.getter.CharacterNamedResultSetGetter;
import org.sfm.jdbc.getter.DoubleIndexedResultSetGetter;
import org.sfm.jdbc.getter.DoubleNamedResultSetGetter;
import org.sfm.jdbc.getter.FloatIndexedResultSetGetter;
import org.sfm.jdbc.getter.FloatNamedResultSetGetter;
import org.sfm.jdbc.getter.IntIndexedResultSetGetter;
import org.sfm.jdbc.getter.IntNamedResultSetGetter;
import org.sfm.jdbc.getter.LongIndexedResultSetGetter;
import org.sfm.jdbc.getter.LongNamedResultSetGetter;
import org.sfm.jdbc.getter.ShortIndexedResultSetGetter;
import org.sfm.jdbc.getter.ShortNamedResultSetGetter;
import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.FieldMapperFactory;
import org.sfm.map.FieldMapperImpl;
import org.sfm.map.GetterFactory;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.primitive.BooleanFieldMapper;
import org.sfm.map.primitive.ByteFieldMapper;
import org.sfm.map.primitive.CharacterFieldMapper;
import org.sfm.map.primitive.DoubleFieldMapper;
import org.sfm.map.primitive.FloatFieldMapper;
import org.sfm.map.primitive.IntFieldMapper;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.map.primitive.ShortFieldMapper;
import org.sfm.reflect.ConstructorOnGetter;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;

public final class ResultSetFieldMapperFactory implements FieldMapperFactory<ResultSet, ColumnKey> {

	private final GetterFactory<ResultSet, ColumnKey>  getterFactory;

	public ResultSetFieldMapperFactory(GetterFactory<ResultSet, ColumnKey> getterFactory) {
		this.getterFactory = getterFactory;
	}

	private <T> FieldMapper<ResultSet, T> primitiveNamedFieldMapper(
			final Setter<T, ?> setter, final ColumnKey key, final FieldMapperErrorHandler<ColumnKey> errorHandler) {
		final Class<?> type = TypeHelper.toClass(setter.getPropertyType());

		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(
					new BooleanNamedResultSetGetter(key.getColumnName()),
					SetterFactory.toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(
					new IntNamedResultSetGetter(key.getColumnName()),
					SetterFactory.toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(
					new LongNamedResultSetGetter(key.getColumnName()),
					SetterFactory.toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(
					new FloatNamedResultSetGetter(key.getColumnName()),
					SetterFactory.toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(
					new DoubleNamedResultSetGetter(key.getColumnName()),
					SetterFactory.toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(
					new ByteNamedResultSetGetter(key.getColumnName()),
					SetterFactory.toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(
					new CharacterNamedResultSetGetter(key.getColumnName()),
					SetterFactory.toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(
					new ShortNamedResultSetGetter(key.getColumnName()),
					SetterFactory.toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}

	private <T> FieldMapper<ResultSet, T> primitiveIndexedFieldMapper(final Setter<T, ?> setter, final ColumnKey key, final FieldMapperErrorHandler<ColumnKey> errorHandler) {
		final Class<?> type = TypeHelper.toClass(setter.getPropertyType());

		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(
					new BooleanIndexedResultSetGetter(key.getColumnIndex()),
					SetterFactory.toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(
					new IntIndexedResultSetGetter(key.getColumnIndex()),
					SetterFactory.toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(
					new LongIndexedResultSetGetter(key.getColumnIndex()),
					SetterFactory.toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(
					new FloatIndexedResultSetGetter(key.getColumnIndex()),
					SetterFactory.toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(
					new DoubleIndexedResultSetGetter(key.getColumnIndex()),
					SetterFactory.toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(
					new ByteIndexedResultSetGetter(key.getColumnIndex()),
					SetterFactory.toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(
					new CharacterIndexedResultSetGetter(key.getColumnIndex()),
					SetterFactory.toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(
					new ShortIndexedResultSetGetter(key.getColumnIndex()),
					SetterFactory.toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}

	@Override
	public <T, P> FieldMapper<ResultSet, T> newFieldMapper(Setter<T, P> setter,
			ColumnKey key, FieldMapperErrorHandler<ColumnKey> errorHandler, MapperBuilderErrorHandler mappingErrorHandler) {
		final Class<?> type = TypeHelper.toClass(setter.getPropertyType());

		if (type.isPrimitive()) {
			if (key.hasColumnIndex()) {
				return primitiveIndexedFieldMapper(setter, key, errorHandler);
			} else {
				return primitiveNamedFieldMapper(setter, key, errorHandler);
			}
		}
		
		Getter<ResultSet, P> getter = getterFactory.newGetter(type, key);
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
			} else if (key.getSqlType() != ColumnKey.UNDEFINED_TYPE) {
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
			mappingErrorHandler.getterNotFound("Could not find getter for " + key + " type " + type);
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
