package org.sfm.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.sfm.jdbc.getter.BooleanNamedResultSetGetter;
import org.sfm.jdbc.getter.ByteNamedResultSetGetter;
import org.sfm.jdbc.getter.CharacterNamedResultSetGetter;
import org.sfm.jdbc.getter.DoubleNamedResultSetGetter;
import org.sfm.jdbc.getter.FloatNamedResultSetGetter;
import org.sfm.jdbc.getter.IntNamedResultSetGetter;
import org.sfm.jdbc.getter.LongNamedResultSetGetter;
import org.sfm.jdbc.getter.ShortNamedResultSetGetter;
import org.sfm.map.FieldMapper;
import org.sfm.map.Mapper;
import org.sfm.map.ObjectFieldMapper;
import org.sfm.map.SaticMapper;
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
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory.newGetter(type, column);
		if (getter == null) {
			throw new IllegalArgumentException("No getter for column " + column + " type " + type);
		}
		return new ObjectFieldMapper<ResultSet, T, Object>(getter, setter);
	}



	private FieldMapper<ResultSet, T> primitiveFieldMapper(String column, Setter<T, Object> setter) {
		Class<?> type = setter.getPropertyType();
		
		if (type.equals(Boolean.TYPE)) {
			return new BooleanFieldMapper<ResultSet, T>(new BooleanNamedResultSetGetter(column), setterFactory.toBooleanSetter(setter));
		} else if (type.equals(Integer.TYPE)) {
			return new IntFieldMapper<ResultSet, T>(new IntNamedResultSetGetter(column), setterFactory.toIntSetter(setter));
		} else if (type.equals(Long.TYPE)) {
			return new LongFieldMapper<ResultSet, T>(new LongNamedResultSetGetter(column), setterFactory.toLongSetter(setter));
		} else if (type.equals(Float.TYPE)) {
			return new FloatFieldMapper<ResultSet, T>(new FloatNamedResultSetGetter(column), setterFactory.toFloatSetter(setter));
		} else if (type.equals(Double.TYPE)) {
			return new DoubleFieldMapper<ResultSet, T>(new DoubleNamedResultSetGetter(column), setterFactory.toDoubleSetter(setter));
		} else if (type.equals(Byte.TYPE)) {
			return new ByteFieldMapper<ResultSet, T>(new ByteNamedResultSetGetter(column), setterFactory.toByteSetter(setter));
		} else if (type.equals(Character.TYPE)) {
			return new CharacterFieldMapper<ResultSet, T>(new CharacterNamedResultSetGetter(column), setterFactory.toCharacterSetter(setter));
		} else if (type.equals(Short.TYPE)) {
			return new ShortFieldMapper<ResultSet, T>(new ShortNamedResultSetGetter(column), setterFactory.toShortSetter(setter));
		} else {
			throw new UnsupportedOperationException("Type " + type + " is not primitive");
		}
	}

	@SuppressWarnings("unchecked")
	public Mapper<ResultSet, T> mapper() {
		if (!fields.isEmpty()) {
			return new SaticMapper<ResultSet, T>(fields.toArray(new FieldMapper[fields.size()]));
		} else {
			return adaptiveMapper();
		}
	}
	
	public Mapper<ResultSet, T> adaptiveMapper() {
		return new ResultSetAdaptiveMapper<T>(this.setterFactory.getAllSetters(target));
	}

	public void addColumn(String column) {
		String name = PropertyHelper.toPropertyName(column);
		addMapping(name, column);
	}



}
