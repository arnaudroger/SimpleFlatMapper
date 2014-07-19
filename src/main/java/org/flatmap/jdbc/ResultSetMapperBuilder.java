package org.flatmap.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.flatmap.jdbc.getter.BooleanNameResultSetGetter;
import org.flatmap.jdbc.getter.ByteNameResultSetGetter;
import org.flatmap.jdbc.getter.CharacterNameResultSetGetter;
import org.flatmap.jdbc.getter.DoubleNameResultSetGetter;
import org.flatmap.jdbc.getter.FloatNameResultSetGetter;
import org.flatmap.jdbc.getter.IntNameResultSetGetter;
import org.flatmap.jdbc.getter.LongNameResultSetGetter;
import org.flatmap.jdbc.getter.ShortNameResultSetGetter;
import org.flatmap.jdbc.getter.StringNameResultSetGetter;
import org.flatmap.jdbc.getter.TimestampNameResultSetGetter;
import org.flatmap.map.FieldMapper;
import org.flatmap.map.Mapper;
import org.flatmap.map.ObjectFieldMapper;
import org.flatmap.map.primitive.BooleanFieldMapper;
import org.flatmap.map.primitive.ByteFieldMapper;
import org.flatmap.map.primitive.CharacterFieldMapper;
import org.flatmap.map.primitive.DoubleFieldMapper;
import org.flatmap.map.primitive.FloatFieldMapper;
import org.flatmap.map.primitive.IntFieldMapper;
import org.flatmap.map.primitive.LongFieldMapper;
import org.flatmap.map.primitive.ShortFieldMapper;
import org.flatmap.reflect.Getter;
import org.flatmap.reflect.ReflectionSetterFactory;
import org.flatmap.reflect.Setter;
import org.flatmap.reflect.SetterFactory;
import org.flatmap.utils.PropertyHelper;

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
		
		if (setter.getType().isPrimitive()) {
			fieldMapper = primitiveFieldMapper(column, setter);
		} else {
			fieldMapper = objectFieldMapper(column, setter);
		}
		
		fields.add(fieldMapper);
		return this;
	}

	private FieldMapper<ResultSet, T> objectFieldMapper(String column,
			Setter<T, Object> setter) {
		Class<? extends Object> type = setter.getType();
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
		Class<?> type = setter.getType();
		
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
