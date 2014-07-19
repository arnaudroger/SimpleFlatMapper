package org.flatmap.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.flatmap.jdbc.getter.BooleanNameResultSetGetter;
import org.flatmap.jdbc.getter.ByteNameResultSetGetter;
import org.flatmap.jdbc.getter.CharacterNameResultSetGetter;
import org.flatmap.jdbc.getter.DoubleNameResultSetGetter;
import org.flatmap.jdbc.getter.FloatNameResultSetGetter;
import org.flatmap.jdbc.getter.IntNameResultSetGetter;
import org.flatmap.jdbc.getter.LongNameResultSetGetter;
import org.flatmap.jdbc.getter.ObjectNameResultSetGetter;
import org.flatmap.jdbc.getter.ShortNameResultSetGetter;
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
			Getter<ResultSet, Object> getter = new ObjectNameResultSetGetter(column);
			fieldMapper = new ObjectFieldMapper<ResultSet, T, Object>(getter, setter);
		}
		
		fields.add(fieldMapper);
		return this;
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
		} 
		
		return null;
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
