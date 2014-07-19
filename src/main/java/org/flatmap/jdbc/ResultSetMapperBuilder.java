package org.flatmap.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.flatmap.jdbc.getter.NameResultSetGetter;
import org.flatmap.map.FieldMapper;
import org.flatmap.map.Mapper;
import org.flatmap.reflect.ReflectionSetterFactory;
import org.flatmap.reflect.Setter;
import org.flatmap.reflect.SetterFactory;

public class ResultSetMapperBuilder<T> {
	
	private final Class<T> target;
	
	private final SetterFactory setterFactory = new ReflectionSetterFactory();
	private List<FieldMapper<ResultSet, T, ?>> fields = new ArrayList<FieldMapper<ResultSet, T, ?>>();
	
	public ResultSetMapperBuilder(Class<T> target) {
		this.target = target;
	}
	
	public ResultSetMapperBuilder<T> addMapping(String property, String column) {
		NameResultSetGetter getter = new NameResultSetGetter(column);
		Setter<T, Object> setter = setterFactory.getSetter(target, property);
		fields.add(new FieldMapper<ResultSet, T, Object>(getter, setter));
		return this;
	}

	@SuppressWarnings("unchecked")
	public Mapper<ResultSet, T> mapper() {
		return new Mapper<ResultSet, T>(fields.toArray(new FieldMapper[fields.size()]));
	}

}
