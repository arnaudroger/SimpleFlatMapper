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
import org.sfm.map.Mapper;
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

public class PrimitiveFieldMapperFactory<T> {

	private final SetterFactory setterFactory;

	public PrimitiveFieldMapperFactory(SetterFactory setterFactory) {
		this.setterFactory = setterFactory;
	}

	public Mapper<ResultSet, T> primitiveFieldMapper(String column,
			Setter<T, Object> setter) {
		Class<?> type = setter.getPropertyType();

		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(
					new BooleanNamedResultSetGetter(column),
					setterFactory.toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(
					new IntNamedResultSetGetter(column),
					setterFactory.toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(
					new LongNamedResultSetGetter(column),
					setterFactory.toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(
					new FloatNamedResultSetGetter(column),
					setterFactory.toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(
					new DoubleNamedResultSetGetter(column),
					setterFactory.toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(
					new ByteNamedResultSetGetter(column),
					setterFactory.toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(
					new CharacterNamedResultSetGetter(column),
					setterFactory.toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(
					new ShortNamedResultSetGetter(column),
					setterFactory.toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}

	public Mapper<ResultSet, T> primitiveFieldMapper(int column,
			Setter<T, Object> setter) {
		Class<?> type = setter.getPropertyType();

		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(
					new BooleanIndexedResultSetGetter(column),
					setterFactory.toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(
					new IntIndexedResultSetGetter(column),
					setterFactory.toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(
					new LongIndexedResultSetGetter(column),
					setterFactory.toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(
					new FloatIndexedResultSetGetter(column),
					setterFactory.toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(
					new DoubleIndexedResultSetGetter(column),
					setterFactory.toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(
					new ByteIndexedResultSetGetter(column),
					setterFactory.toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(
					new CharacterIndexedResultSetGetter(column),
					setterFactory.toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(
					new ShortIndexedResultSetGetter(column),
					setterFactory.toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type
					+ " is not primitive");
		}
	}
}
