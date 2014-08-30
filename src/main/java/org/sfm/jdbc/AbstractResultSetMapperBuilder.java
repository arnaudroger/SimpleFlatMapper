package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.FieldMapperImpl;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.reflect.asm.Parameter;
import org.sfm.utils.PropertyNameMatcher;

public abstract class AbstractResultSetMapperBuilder<T> implements ResultSetMapperBuilder<T> {

	private FieldMapperErrorHandler fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private JdbcMapperErrorHandler jdbcMapperErrorHandler = new RethrowJdbcMapperErrorHandler();
	private final Class<T> target;
	private final PrimitiveFieldMapperFactory<T> primitiveFieldMapperFactory;
	private final AsmFactory asmFactory;
	
	private final InstantiatorFactory instantiatorFactory;

	private final List<FieldMapper<ResultSet, T>> fields = new ArrayList<FieldMapper<ResultSet, T>>();
	
	
	private final List<ConstructorDefinition<T>> constructors;
	private final Map<Parameter, Getter<ResultSet, ?>> constructorInjection;
	
	public AbstractResultSetMapperBuilder(Class<T> target, SetterFactory setterFactory, boolean asmPresent) throws MapperBuildingException {
		this.target = target;
		this.primitiveFieldMapperFactory = new PrimitiveFieldMapperFactory<>(setterFactory);
		this.asmFactory = setterFactory.getAsmFactory();
		this.instantiatorFactory = new InstantiatorFactory(asmFactory);
		
		if (asmPresent) {
			try {
				this.constructors = ConstructorDefinition.extractConstructors(target);
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
			this.constructorInjection = new HashMap<Parameter, Getter<ResultSet,?>>();
		} else {
			this.constructors = null;
			this.constructorInjection = null;
		}
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
	public final ResultSetMapperBuilder<T> addNamedColumn(final String column) {
		return addNamedColumn(column, ResultSetGetterFactory.UNDEFINED);
	}

	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column) {
		return addIndexedColumn(column, fields.size() + constructorInjection.size() + 1);
	}

	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column, final int columnIndex) {
		return addIndexedColumn(column, columnIndex, ResultSetGetterFactory.UNDEFINED);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final String column) {
		return addMapping(property, column, ResultSetGetterFactory.UNDEFINED);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final int column) {
		return addMapping(property, column, ResultSetGetterFactory.UNDEFINED);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addNamedColumn(final String column, final int sqlType) {
		Parameter param = hasMatchingConstructor(column);
		if (param != null) {
			removeNonMatchingConstructor(param);
			constructorInjection.put(param, ResultSetGetterFactory.newGetter(param.getType(), column, sqlType));
		} else {
			final Setter<T, Object> setter = findSetter(column);
			if (setter == null) {
				mapperBuilderErrorHandler.setterNotFound(target, column);
			} else {
				addMapping(setter, column, sqlType);
			}
		}
		return this;
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column, final int columnIndex, final int sqlType) {
		Parameter param = hasMatchingConstructor(column);
		if (param != null) {
			removeNonMatchingConstructor(param);
			constructorInjection.put(param, ResultSetGetterFactory.newGetter(param.getType(), columnIndex, sqlType));
		} else {
			final Setter<T, Object> setter = findSetter(column);
			if (setter == null) {
				mapperBuilderErrorHandler.setterNotFound(target, column);
			} else {
				addMapping(setter, columnIndex, sqlType);
			}
		}
		return this;
	}

	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final String column, final int sqlType) {
		Parameter param = hasMatchingConstructor(column);
		if (param != null) {
			removeNonMatchingConstructor(param);
			constructorInjection.put(param, ResultSetGetterFactory.newGetter(param.getType(), column, sqlType));
		} else {
			final Setter<T, Object> setter = getSetter(property);
			if (setter == null) {
				mapperBuilderErrorHandler.setterNotFound(target, property);
			} else {
				addMapping(setter, column, sqlType);
			}
		}
		return this;
	}

	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String property, final int column, final int sqlType) {
		Parameter param = hasMatchingConstructor(property);
		if (param != null) {
			removeNonMatchingConstructor(param);
			constructorInjection.put(param, ResultSetGetterFactory.newGetter(param.getType(), column, sqlType));
		} else {
			final Setter<T, Object> setter = getSetter(property);
			if (setter == null) {
				mapperBuilderErrorHandler.setterNotFound(target, property);
			} else {
				addMapping(setter, column, sqlType);
			}
		}
		return this;
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final ResultSetMetaData metaData) throws SQLException {
		for(int i = 1; i <= metaData.getColumnCount(); i++) {
			addIndexedColumn(metaData.getColumnLabel(i), i, metaData.getColumnType(i));
		}
		
		return this;
	}
	
	@Override
	public final JdbcMapper<T> mapper() throws MapperBuildingException {
		if (asmFactory != null) {
			try {
				return asmFactory.createJdbcMapper(fields(), getInstantiator(), target, jdbcMapperErrorHandler);
			} catch(Exception e) {
				return new JdbcMapperImpl<T>(fields(), getInstantiator(), jdbcMapperErrorHandler);
			}
		} else {
			return new JdbcMapperImpl<T>(fields(), getInstantiator(), jdbcMapperErrorHandler);
		}
	}

	private Instantiator<ResultSet, T> getInstantiator() throws MapperBuildingException {
		if (constructors == null) {
			try {
				return instantiatorFactory.getInstantiator(ResultSet.class, target);
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		} else {
			try {
				return instantiatorFactory.getInstantiator(ResultSet.class, constructors, constructorInjection);
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		}
	}

	public final Class<T> getTarget() {
		return target;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final FieldMapper<ResultSet, T>[] fields() {
		return fields.toArray(new FieldMapper[fields.size()]);
	}

	private void addMapping(Setter<T, Object> setter, String column, int sqlType) {
		FieldMapper<ResultSet, T> fieldMapper;
	
		if (setter.getPropertyType().isPrimitive()) {
			fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(column, setter, column, fieldMapperErrorHandler);
		} else {
			fieldMapper = objectFieldMapper(column, setter, sqlType);
		}
	
		fields.add(fieldMapper);
	}

	private void addMapping(Setter<T, Object> setter, int column, int sqlType) {
		FieldMapper<ResultSet, T> fieldMapper;
	
		if (setter.getPropertyType().isPrimitive()) {
			fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(column, setter, String.valueOf(column), fieldMapperErrorHandler);
		} else {
			fieldMapper = objectFieldMapper(column, setter, sqlType);
		}
	
		fields.add(fieldMapper);
	}

	private FieldMapper<ResultSet, T> objectFieldMapper(String column, Setter<T, Object> setter, int sqlType) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory.newGetter(type, column, sqlType);
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("No getter for column "
					+ column + " type " + type);
			return null;
		} else {
			return new FieldMapperImpl<ResultSet, T, Object>(column, getter,
					setter, fieldMapperErrorHandler);
		}
	}

	private FieldMapper<ResultSet, T> objectFieldMapper(int column, Setter<T, Object> setter, int sqlType) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory.newGetter(type, column, sqlType);
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("No getter for column "
					+ column + " type " + type);
			return null;
		} else {
			return new FieldMapperImpl<ResultSet, T, Object>(
					String.valueOf(column), getter, setter,
					fieldMapperErrorHandler);
		}
	}
	
	private void removeNonMatchingConstructor(Parameter param) {
		ListIterator<ConstructorDefinition<T>> li = constructors.listIterator();
		
		while(li.hasNext()){
			ConstructorDefinition<T> cd = li.next();
			if (!cd.hasParam(param)) {
				li.remove();
			}
		}
	}

	private Parameter hasMatchingConstructor(String column) {
		if (constructors == null)  return null;
		
		for(ConstructorDefinition<T> cd : constructors) {
			Parameter param = cd.lookFor(new PropertyNameMatcher(column));
			if (param != null) {
				return param;
			}
		}
		return null;
	}
	
	protected abstract Setter<T, Object> findSetter(String column);
	protected abstract  Setter<T, Object> getSetter(String property);


}