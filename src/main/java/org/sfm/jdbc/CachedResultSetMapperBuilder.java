package org.sfm.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.map.SaticMapper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.PropertyNameMatcher;

public class CachedResultSetMapperBuilder<T> {

	private final List<Mapper<ResultSet, T>> fields = new ArrayList<Mapper<ResultSet, T>>();

	private FieldMapperErrorHandler fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();

	private final Map<String, Setter<T, Object>> setters;
	private final Class<T> target;
	private PrimitiveFieldMapperFactory<T> primitiveFieldMapperFactory;

	public CachedResultSetMapperBuilder(Class<T> target,
			Map<String, Setter<T, Object>> setters, SetterFactory setterFactory) {
		this.setters = setters;
		this.target = target;
		this.primitiveFieldMapperFactory = new PrimitiveFieldMapperFactory<T>(
				setterFactory);
	}

	public CachedResultSetMapperBuilder<T> fieldMapperErrorHandler(
			FieldMapperErrorHandler errorHandler) {
		if (!fields.isEmpty()) {
			throw new IllegalStateException(
					"Error Handler need to be set before adding fields");
		}
		fieldMapperErrorHandler = errorHandler;
		return this;
	}

	public CachedResultSetMapperBuilder<T> mapperBuilderErrorHandler(
			MapperBuilderErrorHandler errorHandler) {
		mapperBuilderErrorHandler = errorHandler;
		return this;
	}

	public CachedResultSetMapperBuilder<T> addIndexedColumn(String column) {
		return addIndexedColumn(column, fields.size() + 1);
	}

	public CachedResultSetMapperBuilder<T> addIndexedColumn(String column, int p) {
		Setter<T, Object> setter = findSetter(new PropertyNameMatcher(column));
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, column);
		} else {
			addMapping(setter, p);
		}
		return this;
	}

	private Setter<T, Object> findSetter(PropertyNameMatcher propertyNameMatcher) {
		for (Entry<String, Setter<T, Object>> e : setters.entrySet()) {
			if (propertyNameMatcher.matches(e.getKey())) {
				return e.getValue();
			}
		}
		return null;
	}

	public Mapper<ResultSet, T> mapper() {
		return new SaticMapper<ResultSet, T>(fields());
	}

	@SuppressWarnings("unchecked")
	public Mapper<ResultSet, T>[] fields() {
		return fields.toArray(new Mapper[fields.size()]);
	}
	

	private void addMapping(Setter<T, Object> setter, int column) {
		Mapper<ResultSet, T> fieldMapper;

		if (setter.getPropertyType().isPrimitive()) {
			fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(
					column, setter);
		} else {
			fieldMapper = objectFieldMapper(column, setter);
		}

		fields.add(fieldMapper);
	}

	private Mapper<ResultSet, T> objectFieldMapper(int column,
			Setter<T, Object> setter) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory
				.newGetter(type, column);
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("No getter for column "
					+ column + " type " + type);
			return null;
		} else {
			return new FieldMapper<ResultSet, T, Object>(
					String.valueOf(column), getter, setter,
					fieldMapperErrorHandler);
		}
	}

}
