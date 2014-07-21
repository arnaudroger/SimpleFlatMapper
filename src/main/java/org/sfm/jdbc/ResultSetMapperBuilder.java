package org.sfm.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.sfm.jdbc.getter.BooleanNameResultSetGetter;
import org.sfm.jdbc.getter.ByteNameResultSetGetter;
import org.sfm.jdbc.getter.CharacterNameResultSetGetter;
import org.sfm.jdbc.getter.DoubleNameResultSetGetter;
import org.sfm.jdbc.getter.FloatNameResultSetGetter;
import org.sfm.jdbc.getter.IntNameResultSetGetter;
import org.sfm.jdbc.getter.LongNameResultSetGetter;
import org.sfm.jdbc.getter.ShortNameResultSetGetter;
import org.sfm.jdbc.getter.StringNameResultSetGetter;
import org.sfm.jdbc.getter.TimestampNameResultSetGetter;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.ObjectFieldMapper;
import org.sfm.map.primitive.BooleanFieldMapper;
import org.sfm.map.primitive.ByteFieldMapper;
import org.sfm.map.primitive.CharacterFieldMapper;
import org.sfm.map.primitive.DoubleFieldMapper;
import org.sfm.map.primitive.FloatFieldMapper;
import org.sfm.map.primitive.IntFieldMapper;
import org.sfm.map.primitive.LongFieldMapper;
import org.sfm.map.primitive.ShortFieldMapper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionSetterFactory;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.PropertyHelper;

public class ResultSetMapperBuilder<T> {
	
	private final Class<T> target;
	
	private final SetterFactory setterFactory = new ReflectionSetterFactory();
	private List<FieldMapper<ResultSet, T>> fields = new ArrayList<FieldMapper<ResultSet, T>>();
	
	public ResultSetMapperBuilder(Class<T> target) {
		this.target = target;
	}
	
	public ResultSetMapperBuilder<T> addMapping(String property, String column) {
		Setter<T, Object> setter = setterFactory.getSetter(target, property);
		
		FieldMapper<ResultSet, T> fieldMapper;
		
		if (setter.getPropertyType().isPrimitive()) {
			fieldMapper = primitiveFieldMapper(column, setter);
		} else {
			fieldMapper = objectFieldMapper(column, setter);
		}
		
		fields.add(fieldMapper);
		return this;
	}

	private FieldMapper<ResultSet, T> objectFieldMapper(String column,
			Setter<T, Object> setter) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = getResultSetGetterForType(type, column);
		return new ObjectFieldMapper<ResultSet, T, Object>(getter, setter);
	}

	private Getter<ResultSet, ? extends Object> getResultSetGetterForType(
			Class<? extends Object> type, String column) {
		Getter<ResultSet, ? extends Object> getter;
		if (type.isAssignableFrom(String.class)) {
			getter = new StringNameResultSetGetter(column);
		} else if (type.isAssignableFrom(Date.class)) {
			getter = new TimestampNameResultSetGetter(column);
		} else if (type.equals(Boolean.class)) {
			getter = new BooleanNameResultSetGetter(column);
		} else if (type.equals(Integer.class)) {
			getter = new IntNameResultSetGetter(column);
		} else if (type.equals(Long.class)) {
			getter = new LongNameResultSetGetter(column);
		} else if (type.equals(Float.class)) {
			getter = new FloatNameResultSetGetter(column);
		} else if (type.equals(Double.class)) {
			getter = new DoubleNameResultSetGetter(column);
		} else if (type.equals(Byte.class)) {
			getter = new ByteNameResultSetGetter(column);
		} else if (type.equals(Character.class)) {
			getter = new CharacterNameResultSetGetter(column);
		} else if (type.equals(Short.class)) {
			getter = new ShortNameResultSetGetter(column);
		} else {
			throw new UnsupportedOperationException("Unsupported Object type " + type);
		}
		return getter;
	}

	private FieldMapper<ResultSet, T> primitiveFieldMapper(String column, Setter<T, Object> setter) {
		Class<?> type = setter.getPropertyType();
		
		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(new BooleanNameResultSetGetter(column), setterFactory.toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(new IntNameResultSetGetter(column), setterFactory.toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(new LongNameResultSetGetter(column), setterFactory.toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(new FloatNameResultSetGetter(column), setterFactory.toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(new DoubleNameResultSetGetter(column), setterFactory.toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(new ByteNameResultSetGetter(column), setterFactory.toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(new CharacterNameResultSetGetter(column), setterFactory.toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(new ShortNameResultSetGetter(column), setterFactory.toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type + " is not primitive");
		}
	}

	@SuppressWarnings("unchecked")
	public Mapper<ResultSet, T> mapper() {
		return new Mapper<ResultSet, T>(fields.toArray(new FieldMapper[fields.size()]));
	}

	public void addColumn(String column) {
		String name = PropertyHelper.toPropertyName(column);
		addMapping(name, column);
	}

}
