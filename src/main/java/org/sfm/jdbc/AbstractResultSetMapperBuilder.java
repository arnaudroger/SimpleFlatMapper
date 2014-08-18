package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
import org.sfm.reflect.asm.AsmFactory;

public abstract class AbstractResultSetMapperBuilder<T> implements ResultSetMapperBuilder<T> {

	private FieldMapperErrorHandler fieldMapperErrorHandler = new LogFieldMapperErrorHandler();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();

	private final Class<T> target;
	private final PrimitiveFieldMapperFactory<T> primitiveFieldMapperFactory;
	private final AsmFactory asmFactory;

	private final List<Mapper<ResultSet, T>> fields = new ArrayList<Mapper<ResultSet, T>>();
	
	public AbstractResultSetMapperBuilder(Class<T> target, SetterFactory setterFactory) {
		this.target = target;
		this.primitiveFieldMapperFactory = new PrimitiveFieldMapperFactory<>(setterFactory);
		this.asmFactory = setterFactory.getAsmFactory();
	}

	@Override
	public ResultSetMapperBuilder<T> fieldMapperErrorHandler(FieldMapperErrorHandler errorHandler) {
		if (!fields.isEmpty()) {
			throw new IllegalStateException(
					"Error Handler need to be set before adding fields");
		}
		fieldMapperErrorHandler = errorHandler;
		return this;
	}

	@Override
	public ResultSetMapperBuilder<T> mapperBuilderErrorHandler(MapperBuilderErrorHandler errorHandler) {
		mapperBuilderErrorHandler = errorHandler;
		return this;
	}

	@Override
	public ResultSetMapperBuilder<T> addNamedColumn(String column, int sqlType) {
		Setter<T, Object> setter = findSetter(column);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, column);
		} else {
			addMapping(setter, column, sqlType);
		}
		return this;
	}
	
	@Override
	public ResultSetMapperBuilder<T> addNamedColumn(String column) {
		return addNamedColumn(column, ResultSetGetterFactory.UNDEFINED);
	}

	@Override
	public ResultSetMapperBuilder<T> addIndexedColumn(String column) {
		return addIndexedColumn(column, fields.size() + 1);
	}

	@Override
	public ResultSetMapperBuilder<T> addIndexedColumn(String column, int columnIndex) {
		return addIndexedColumn(column, columnIndex, ResultSetGetterFactory.UNDEFINED);
	}
	
	@Override
	public ResultSetMapperBuilder<T> addIndexedColumn(String column, int columnIndex, int sqlType) {
		Setter<T, Object> setter = findSetter(column);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, column);
		} else {
			addMapping(setter, columnIndex, sqlType);
		}
		return this;
	}

	@Override
	public ResultSetMapperBuilder<T> addMapping(String property, String column, int sqlType) {
		Setter<T, Object> setter = getSetter(property);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, property);
		} else {
			addMapping(setter, column, sqlType);
		}
		return this;
	}
	
	@Override
	public ResultSetMapperBuilder<T> addMapping(String property, String column) {
		return addMapping(property, column, ResultSetGetterFactory.UNDEFINED);
	}

	@Override
	public ResultSetMapperBuilder<T> addMapping(String property, int column, int sqlType) {
		Setter<T, Object> setter = getSetter(property);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, property);
		} else {
			addMapping(setter, column, sqlType);
		}
		return this;
	}
	
	@Override
	public ResultSetMapperBuilder<T> addMapping(String property, int column) {
		return addMapping(property, column, ResultSetGetterFactory.UNDEFINED);
	}

	@Override
	public ResultSetMapperBuilder<T> addMapping(ResultSetMetaData metaData) throws SQLException {
		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			addIndexedColumn(metaData.getColumnName(i), i, metaData.getColumnType(i));
		}
		
		return this;
	}
	
	@Override
	public Mapper<ResultSet, T> mapper() {
		if (asmFactory != null) {
			try {
				return asmFactory.createMapper(fields(), ResultSet.class, target);
			} catch(Exception e) {
				return new SaticMapper<ResultSet, T>(fields());
			}
		} else {
			return new SaticMapper<ResultSet, T>(fields());
		}
	}

	public Class<T> getTarget() {
		return target;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Mapper<ResultSet, T>[] fields() {
		return fields.toArray(new Mapper[fields.size()]);
	}

	private void addMapping(Setter<T, Object> setter, String column, int sqlType) {
		Mapper<ResultSet, T> fieldMapper;
	
		if (setter.getPropertyType().isPrimitive()) {
			fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(column, setter, column, fieldMapperErrorHandler);
		} else {
			fieldMapper = objectFieldMapper(column, setter, sqlType);
		}
	
		fields.add(fieldMapper);
	}

	private void addMapping(Setter<T, Object> setter, int column, int sqlType) {
		Mapper<ResultSet, T> fieldMapper;
	
		if (setter.getPropertyType().isPrimitive()) {
			fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(column, setter, String.valueOf(column), fieldMapperErrorHandler);
		} else {
			fieldMapper = objectFieldMapper(column, setter, sqlType);
		}
	
		fields.add(fieldMapper);
	}

	private Mapper<ResultSet, T> objectFieldMapper(String column, Setter<T, Object> setter, int sqlType) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory.newGetter(type, column, sqlType);
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("No getter for column "
					+ column + " type " + type);
			return null;
		} else {
			return new FieldMapper<ResultSet, T, Object>(column, getter,
					setter, fieldMapperErrorHandler);
		}
	}

	private Mapper<ResultSet, T> objectFieldMapper(int column, Setter<T, Object> setter, int sqlType) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory.newGetter(type, column, sqlType);
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
	
	protected abstract Setter<T, Object> findSetter(String column);
	protected abstract  Setter<T, Object> getSetter(String property);


}