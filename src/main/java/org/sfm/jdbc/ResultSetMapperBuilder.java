package org.sfm.jdbc;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.LogFieldMapperErrorHandler;
import org.sfm.map.Mapper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.map.SaticMapper;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.utils.PropertyNameMatcher;

public class ResultSetMapperBuilder<T> {

	private final Class<T> target;

	private final SetterFactory setterFactory;
	private final List<Mapper<ResultSet, T>> fields = new ArrayList<Mapper<ResultSet, T>>();

	private FieldMapperErrorHandler fieldMapperErrorHandler = new LogFieldMapperErrorHandler();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	
	private PrimitiveFieldMapperFactory<T> primitiveFieldMapperFactory;

	public ResultSetMapperBuilder(Class<T> target) {
		this(target, new SetterFactory());
	}

	public ResultSetMapperBuilder(Class<T> target, SetterFactory setterFactory) {
		this.target = target;
		this.setterFactory = setterFactory;
		primitiveFieldMapperFactory = new PrimitiveFieldMapperFactory<>(setterFactory);
	}

	public ResultSetMapperBuilder<T> fieldMapperErrorHandler(
			FieldMapperErrorHandler errorHandler) {
		if (!fields.isEmpty()) {
			throw new IllegalStateException(
					"Error Handler need to be set before adding fields");
		}
		fieldMapperErrorHandler = errorHandler;
		return this;
	}

	public ResultSetMapperBuilder<T> mapperBuilderErrorHandler(
			MapperBuilderErrorHandler errorHandler) {
		mapperBuilderErrorHandler = errorHandler;
		return this;
	}

	public ResultSetMapperBuilder<T> addNamedColumn(String column) {
		Setter<T, Object> setter = setterFactory.findSetter(
				new PropertyNameMatcher(column), target);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, column);
		} else {
			addMapping(setter, column);
		}
		return this;
	}

	public ResultSetMapperBuilder<T> addIndexedColumn(String column) {
		return addIndexedColumn(column, fields.size() + 1);
	}

	public ResultSetMapperBuilder<T> addIndexedColumn(String column, int p) {
		Setter<T, Object> setter = setterFactory.findSetter(
				new PropertyNameMatcher(column), target);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, column);
		} else {
			addMapping(setter, p);
		}
		return this;
	}

	public ResultSetMapperBuilder<T> addMapping(String property, String column) {
		Setter<T, Object> setter = setterFactory.getSetter(target, property);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, property);
		} else {
			addMapping(setter, column);
		}
		return this;
	}

	public ResultSetMapperBuilder<T> addMapping(String property, int column) {
		Setter<T, Object> setter = setterFactory.getSetter(target, property);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, property);
		} else {
			addMapping(setter, column);
		}
		return this;
	}

	public Mapper<ResultSet, T> mapper() {
		return new SaticMapper<ResultSet, T>(fields());
	}

	@SuppressWarnings("unchecked")
	public Mapper<ResultSet, T>[] fields() {
		return fields.toArray(new Mapper[fields.size()]);
	}

	private void addMapping(Setter<T, Object> setter, String column) {
		Mapper<ResultSet, T> fieldMapper;

		if (setter.getPropertyType().isPrimitive()) {
			fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(column, setter);
		} else {
			fieldMapper = objectFieldMapper(column, setter);
		}

		fields.add(fieldMapper);
	}

	private void addMapping(Setter<T, Object> setter, int column) {
		Mapper<ResultSet, T> fieldMapper;

		if (setter.getPropertyType().isPrimitive()) {
			fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(column, setter);
		} else {
			fieldMapper = objectFieldMapper(column, setter);
		}

		fields.add(fieldMapper);
	}

	private Mapper<ResultSet, T> objectFieldMapper(String column,
			Setter<T, Object> setter) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory
				.newGetter(type, column);
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("No getter for column "
					+ column + " type " + type);
			return null;
		} else {
			return new FieldMapper<ResultSet, T, Object>(column, getter,
					setter, fieldMapperErrorHandler);
		}
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
