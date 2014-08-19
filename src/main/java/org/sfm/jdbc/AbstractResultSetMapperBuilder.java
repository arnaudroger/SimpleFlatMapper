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
	public final ResultSetMapperBuilder<T> fieldMapperErrorHandler(final FieldMapperErrorHandler errorHandler) {
		if (!fields.isEmpty()) {
			throw new IllegalStateException(
					"Error Handler need to be set before adding fields");
		}
		fieldMapperErrorHandler = errorHandler;
		return this;
	}

	@Override
	public final ResultSetMapperBuilder<T> mapperBuilderErrorHandler(final MapperBuilderErrorHandler errorHandler) {
		mapperBuilderErrorHandler = errorHandler;
		return this;
	}

	@Override
	public final ResultSetMapperBuilder<T> addNamedColumn(final String column, final int sqlType) {
		final Setter<T, Object> setter = findSetter(column);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, column);
		} else {
			addMapping(setter, column, sqlType);
		}
		return this;
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addNamedColumn(final String column) {
		return addNamedColumn(column, ResultSetGetterFactory.UNDEFINED);
	}

	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column) {
		return addIndexedColumn(column, fields.size() + 1);
	}

	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column, final int columnIndex) {
		return addIndexedColumn(column, columnIndex, ResultSetGetterFactory.UNDEFINED);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column, final int columnIndex, final int sqlType) {
		final Setter<T, Object> setter = findSetter(column);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, column);
		} else {
			addMapping(setter, columnIndex, sqlType);
		}
		return this;
	}

	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final String column, final int sqlType) {
		final Setter<T, Object> setter = getSetter(property);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, property);
		} else {
			addMapping(setter, column, sqlType);
		}
		return this;
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final String column) {
		return addMapping(property, column, ResultSetGetterFactory.UNDEFINED);
	}

	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final int column, final int sqlType) {
		final Setter<T, Object> setter = getSetter(property);
		if (setter == null) {
			mapperBuilderErrorHandler.setterNotFound(target, property);
		} else {
			addMapping(setter, column, sqlType);
		}
		return this;
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final int column) {
		return addMapping(property, column, ResultSetGetterFactory.UNDEFINED);
	}

	@Override
	public final ResultSetMapperBuilder<T> addMapping(final ResultSetMetaData metaData) throws SQLException {
		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			addIndexedColumn(metaData.getColumnName(i), i, metaData.getColumnType(i));
		}
		
		return this;
	}
	
	@Override
	public final Mapper<ResultSet, T> mapper() {
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

	public final Class<T> getTarget() {
		return target;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final Mapper<ResultSet, T>[] fields() {
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