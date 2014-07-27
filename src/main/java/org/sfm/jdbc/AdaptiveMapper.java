package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.Map;

import org.sfm.map.Mapper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.utils.PropertyNameMatcher;

public class AdaptiveMapper<T> implements Mapper<ResultSet, T> {

	private final Map<String, Setter<T, Object>> setters;
	
	public AdaptiveMapper(Map<String, Setter<T, Object>> setters) {
		this.setters = setters; 
	}

	@Override
	public void map(ResultSet source, T target) throws Exception {
		ResultSetMetaData metaData = source.getMetaData();
		for(int i = 1 ; i <= metaData.getColumnCount(); i++) {
			String columnName = metaData.getColumnName(i);
			String propertyName = PropertyNameMatcher.toPropertyName(columnName);
			Setter<T, Object> setter = setters.get(propertyName);
			if (setter != null) {
				Getter<ResultSet, ?> getter = ResultSetGetterFactory.newGetter(setter.getPropertyType(), i);
				if (getter != null) {
					setter.set(target, getter.get(source));
				}
			}
		}
	}

}
