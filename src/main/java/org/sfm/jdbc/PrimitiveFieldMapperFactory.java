package org.sfm.jdbc;

import java.sql.ResultSet;

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
import org.sfm.map.primitive.BooleanFieldMapper;
import org.sfm.map.primitive.ByteFieldMapper;
import org.sfm.map.primitive.CharacterFieldMapper;
import org.sfm.map.primitive.DoubleFieldMapper;
import org.sfm.map.primitive.FloatFieldMapper;
import org.sfm.map.primitive.IntFieldMapper;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.map.primitive.ShortFieldMapper;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.TypeHelper;

public final class PrimitiveFieldMapperFactory<T> {

	private final SetterFactory setterFactory;

	public PrimitiveFieldMapperFactory(final SetterFactory setterFactory) {
		this.setterFactory = setterFactory;
	}

	public FieldMapper<ResultSet, T> primitiveFieldMapper(final String column,
			final Setter<T, ?> setter, final String name, final FieldMapperErrorHandler errorHandler) {
		final Class<?> type = TypeHelper.toClass(setter.getPropertyType());

		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(name, 
					new BooleanNamedResultSetGetter(column),
					setterFactory.toBooleanSetter(setter), errorHandler);
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(name, 
					new IntNamedResultSetGetter(column),
					setterFactory.toIntSetter(setter), errorHandler);
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(name, 
					new LongNamedResultSetGetter(column),
					setterFactory.toLongSetter(setter), errorHandler);
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(name, 
					new FloatNamedResultSetGetter(column),
					setterFactory.toFloatSetter(setter), errorHandler);
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(name, 
					new DoubleNamedResultSetGetter(column),
					setterFactory.toDoubleSetter(setter), errorHandler);
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(name, 
					new ByteNamedResultSetGetter(column),
					setterFactory.toByteSetter(setter), errorHandler);
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(name, 
					new CharacterNamedResultSetGetter(column),
					setterFactory.toCharacterSetter(setter), errorHandler);
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(name, 
					new ShortNamedResultSetGetter(column),
					setterFactory.toShortSetter(setter), errorHandler);
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}

	public FieldMapper<ResultSet, T> primitiveFieldMapper(final int column,
			final Setter<T, ?> setter, final String name, final FieldMapperErrorHandler errorHandler) {
		final Class<?> type = TypeHelper.toClass(setter.getPropertyType());

		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(name, 
					new BooleanIndexedResultSetGetter(column),
					setterFactory.toBooleanSetter(setter), errorHandler);
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(name, 
					new IntIndexedResultSetGetter(column),
					setterFactory.toIntSetter(setter), errorHandler);
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(name, 
					new LongIndexedResultSetGetter(column),
					setterFactory.toLongSetter(setter), errorHandler);
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(name, 
					new FloatIndexedResultSetGetter(column),
					setterFactory.toFloatSetter(setter), errorHandler);
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(name, 
					new DoubleIndexedResultSetGetter(column),
					setterFactory.toDoubleSetter(setter), errorHandler);
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(name, 
					new ByteIndexedResultSetGetter(column),
					setterFactory.toByteSetter(setter), errorHandler);
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(name, 
					new CharacterIndexedResultSetGetter(column),
					setterFactory.toCharacterSetter(setter), errorHandler);
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(name, 
					new ShortIndexedResultSetGetter(column),
					setterFactory.toShortSetter(setter), errorHandler);
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}
}
