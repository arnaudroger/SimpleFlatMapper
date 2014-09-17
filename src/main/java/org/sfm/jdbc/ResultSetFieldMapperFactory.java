package org.sfm.jdbc;

import java.sql.ResultSet;

import org.sfm.builder.FieldMapperFactory;
import org.sfm.builder.GetterFactory;
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
import org.sfm.map.FieldMapperImpl;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.primitive.BooleanFieldMapper;
import org.sfm.map.primitive.ByteFieldMapper;
import org.sfm.map.primitive.CharacterFieldMapper;
import org.sfm.map.primitive.DoubleFieldMapper;
import org.sfm.map.primitive.FloatFieldMapper;
import org.sfm.map.primitive.IntFieldMapper;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.map.primitive.ShortFieldMapper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;

public final class ResultSetFieldMapperFactory implements FieldMapperFactory<ResultSet, ColumnKey> {

	private final SetterFactory setterFactory;
	private final GetterFactory<ResultSet, ColumnKey>  getterFactory;

	public ResultSetFieldMapperFactory(final SetterFactory setterFactory, GetterFactory<ResultSet, ColumnKey> getterFactory) {
		this.setterFactory = setterFactory;
		this.getterFactory = getterFactory;
	}

	private <T> FieldMapper<ResultSet, T> primitiveNamedFieldMapper(
			final Setter<T, ?> setter, final ColumnKey key, final FieldMapperErrorHandler<ColumnKey> errorHandler) {
		final Class<?> type = TypeHelper.toClass(setter.getPropertyType());

		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T, ColumnKey>(key, 
					new BooleanNamedResultSetGetter(key.getColumnName()),
					setterFactory.toBooleanSetter(setter), errorHandler);
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T, ColumnKey>(key, 
					new IntNamedResultSetGetter(key.getColumnName()),
					setterFactory.toIntSetter(setter), errorHandler);
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T, ColumnKey>(key, 
					new LongNamedResultSetGetter(key.getColumnName()),
					setterFactory.toLongSetter(setter), errorHandler);
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T, ColumnKey>(key, 
					new FloatNamedResultSetGetter(key.getColumnName()),
					setterFactory.toFloatSetter(setter), errorHandler);
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T, ColumnKey>(key, 
					new DoubleNamedResultSetGetter(key.getColumnName()),
					setterFactory.toDoubleSetter(setter), errorHandler);
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T, ColumnKey>(key, 
					new ByteNamedResultSetGetter(key.getColumnName()),
					setterFactory.toByteSetter(setter), errorHandler);
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T, ColumnKey>(key, 
					new CharacterNamedResultSetGetter(key.getColumnName()),
					setterFactory.toCharacterSetter(setter), errorHandler);
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T, ColumnKey>(key, 
					new ShortNamedResultSetGetter(key.getColumnName()),
					setterFactory.toShortSetter(setter), errorHandler);
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}

	private <T> FieldMapper<ResultSet, T> primitiveIndexedFieldMapper(final Setter<T, ?> setter, final ColumnKey key, final FieldMapperErrorHandler<ColumnKey> errorHandler) {
		final Class<?> type = TypeHelper.toClass(setter.getPropertyType());

		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T, ColumnKey>(key, 
					new BooleanIndexedResultSetGetter(key.getColumnIndex()),
					setterFactory.toBooleanSetter(setter), errorHandler);
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T, ColumnKey>(key, 
					new IntIndexedResultSetGetter(key.getColumnIndex()),
					setterFactory.toIntSetter(setter), errorHandler);
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T, ColumnKey>(key, 
					new LongIndexedResultSetGetter(key.getColumnIndex()),
					setterFactory.toLongSetter(setter), errorHandler);
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T, ColumnKey>(key, 
					new FloatIndexedResultSetGetter(key.getColumnIndex()),
					setterFactory.toFloatSetter(setter), errorHandler);
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T, ColumnKey>(key, 
					new DoubleIndexedResultSetGetter(key.getColumnIndex()),
					setterFactory.toDoubleSetter(setter), errorHandler);
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T, ColumnKey>(key, 
					new ByteIndexedResultSetGetter(key.getColumnIndex()),
					setterFactory.toByteSetter(setter), errorHandler);
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T, ColumnKey>(key, 
					new CharacterIndexedResultSetGetter(key.getColumnIndex()),
					setterFactory.toCharacterSetter(setter), errorHandler);
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T, ColumnKey>(key, 
					new ShortIndexedResultSetGetter(key.getColumnIndex()),
					setterFactory.toShortSetter(setter), errorHandler);
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
			mappingErrorHandler.getterNotFound("Could not find getter for " + key + " type " + type);
		}
		return new FieldMapperImpl<ResultSet, T, P, ColumnKey>(key, getter, setter, errorHandler);
	}

}
