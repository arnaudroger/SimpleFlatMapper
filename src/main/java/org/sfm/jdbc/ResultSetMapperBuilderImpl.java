package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.FieldMapperImpl;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.reflect.ClassMeta;
import org.sfm.reflect.ConstructorPropertyMeta;
import org.sfm.reflect.Getter;
import org.sfm.reflect.Instantiator;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.PropertyMeta;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.SubPropertyMeta;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.ConstructorParameter;

public final class ResultSetMapperBuilderImpl<T> implements ResultSetMapperBuilder<T> {

	private FieldMapperErrorHandler fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private JdbcMapperErrorHandler jdbcMapperErrorHandler = new RethrowJdbcMapperErrorHandler();
	
	private final Class<T> target;
	private final ClassMeta<T> classMeta;
	
	private final PrimitiveFieldMapperFactory<T> primitiveFieldMapperFactory;
	private final AsmFactory asmFactory;
	private final InstantiatorFactory instantiatorFactory;

	private final List<FieldMapper<ResultSet, T>> fields = new ArrayList<FieldMapper<ResultSet, T>>();
	private final Map<ConstructorParameter, Getter<ResultSet, ?>> constructorInjections;
	
	private final List<SubProperty<T>> subProperties = new ArrayList<>();
	
	private final SetterFactory setterFactory;
	private final boolean asmPresent;
	
	private int columnIndex = 1;
	
	public ResultSetMapperBuilderImpl(final Class<T> target) throws MapperBuildingException {
		this(target, new SetterFactory(), AsmHelper.isAsmPresent());
	}
	public ResultSetMapperBuilderImpl(final Class<T> target, final SetterFactory setterFactory, final boolean asmPresent) throws MapperBuildingException {
		this(target, new ClassMeta<T>(target, setterFactory, asmPresent), setterFactory, asmPresent);
	}
	
	public ResultSetMapperBuilderImpl(final Class<T> target, final ClassMeta<T> classMeta, final SetterFactory setterFactory, final boolean asmPresent) throws MapperBuildingException {
		this.target = target;
		this.primitiveFieldMapperFactory = new PrimitiveFieldMapperFactory<>(setterFactory);
		this.asmFactory = setterFactory.getAsmFactory();
		this.instantiatorFactory = new InstantiatorFactory(asmFactory);
		this.classMeta = classMeta;
		this.setterFactory = setterFactory;
		this.asmPresent = asmPresent;
		this.constructorInjections = new HashMap<ConstructorParameter, Getter<ResultSet,?>>();
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
		return addIndexedColumn(column, columnIndex ++);
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
		return addMapping(column, column, sqlType);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addIndexedColumn(final String column, final int columnIndex, final int sqlType) {
		return addMapping(column, column, columnIndex, sqlType);
	}

	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String propertyName, final int columnIndex, final int sqlType) {
		return addMapping(propertyName, "column:"+ columnIndex, columnIndex, sqlType);
	}
	
	@Override
	public final ResultSetMapperBuilder<T> addMapping(final String propertyName, final String column, final int sqlType) {
		final PropertyMeta<T, ?> property = classMeta.findProperty(propertyName);
		if (property == null) {
			mapperBuilderErrorHandler.setterNotFound(target, propertyName);
		} else {
			addMapping(property, column, sqlType);
		}
		return this;
	}
	
	public final ResultSetMapperBuilder<T> addMapping(final String propertyName, final String columnName, final int columnIndex, final int sqlType) {
		final PropertyMeta<T, ?> property = classMeta.findProperty(propertyName);
		if (property == null) {
				mapperBuilderErrorHandler.setterNotFound(target, propertyName);
		} else {
			addMapping(property, columnName, columnIndex, sqlType);
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
		if (!asmPresent) {
			try {
				return instantiatorFactory.getInstantiator(ResultSet.class, target);
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		} else {
			try {
				return instantiatorFactory.getInstantiator(ResultSet.class, classMeta.getConstructorDefinitions(), constructorInjections());
			} catch(Exception e) {
				throw new MapperBuildingException(e.getMessage(), e);
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Map<ConstructorParameter, Getter<ResultSet, ?>> constructorInjections() {
		Map<ConstructorParameter, Getter<ResultSet, ?>> injections = new HashMap<>(constructorInjections);
		
		for(SubProperty<T> subProp : subProperties) {
			PropertyMeta<T, ?> prop = subProp.subProperty.getProperty();
			if (prop instanceof ConstructorPropertyMeta) {
				ResultSetMapperBuilderImpl<?> builder = subProp.mapperBuilder;
				
				final JdbcMapper<T> mapper = (JdbcMapper<T>) builder.mapper();
				
				Getter<ResultSet, T> getter = new JdbcMapperGetterAdapter<>(mapper); 
				
				injections.put(((ConstructorPropertyMeta) prop).getConstructorParameter(), getter);
			}
			
		}
		
		return injections;
	}
	public final Class<T> getTarget() {
		return target;
	}

	@Override
	@SuppressWarnings("unchecked")
	public final FieldMapper<ResultSet, T>[] fields() {
		List<FieldMapper<ResultSet, T>> fields = new ArrayList<>(this.fields);
		
		for(SubProperty<T> subProp : subProperties) {
			PropertyMeta<T, ?> prop = subProp.subProperty.getProperty();
			if (!(prop instanceof ConstructorPropertyMeta)) {
				Setter<T, Object> setter = (Setter<T, Object>) prop.getSetter();
				
				JdbcMapper<T> mapper = (JdbcMapper<T>) subProp.mapperBuilder.mapper();
				
				Getter<ResultSet, T> getter = new JdbcMapperGetterAdapter<>(mapper);
				
				fields.add(new FieldMapperImpl<ResultSet, T, Object>(prop.getName(), getter, setter, fieldMapperErrorHandler));
			}
			
		}
		
		return fields.toArray(new FieldMapper[fields.size()]);
	}

	private SubProperty<T> getOrAddSubPropertyMapperBuilder(SubPropertyMeta<T, ?> property) {
		
		for(SubProperty<T> subProp : subProperties) {
			if (subProp.subProperty.getName().equals(property.getName())) {
				return subProp;
			}
 		}
		
		ResultSetMapperBuilderImpl<T> builder = new ResultSetMapperBuilderImpl<>(property.getType(), property.getClassMeta(setterFactory, asmPresent), setterFactory, asmPresent);
		SubProperty<T> subProp = new SubProperty<T>(builder, property);
		
		subProperties.add(subProp);
		
		return subProp;
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addMapping(PropertyMeta<T, ?> property, String column, int sqlType) {
		if (property instanceof ConstructorPropertyMeta) {
			ConstructorParameter constructorParameter = ((ConstructorPropertyMeta) property).getConstructorParameter();
			constructorInjections.put(constructorParameter, ResultSetGetterFactory.newGetter(constructorParameter.getType(), column, sqlType));
		} else if (property instanceof SubPropertyMeta) {
			SubProperty<T> subProp = getOrAddSubPropertyMapperBuilder((SubPropertyMeta)property);
			subProp.mapperBuilder.addMapping(((SubPropertyMeta) property).getSubProperty(), column, sqlType);
		} else if (property.getType().isPrimitive()) {
			FieldMapper<ResultSet, T> fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(column, property.getSetter(), column, fieldMapperErrorHandler);
			fields.add(fieldMapper);
		} else {
			FieldMapper<ResultSet, T> fieldMapper = objectFieldMapper(column, property.getSetter(), sqlType);
			fields.add(fieldMapper);
		}
	
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addMapping(PropertyMeta<T, ?> property, String columnName, int column, int sqlType) {
		if (property instanceof ConstructorPropertyMeta) {
			ConstructorParameter constructorParameter = ((ConstructorPropertyMeta) property).getConstructorParameter();
			constructorInjections.put(constructorParameter, ResultSetGetterFactory.newGetter(constructorParameter.getType(), column, sqlType));
		} else if (property instanceof SubPropertyMeta) {
			SubProperty<T> subProp = getOrAddSubPropertyMapperBuilder((SubPropertyMeta)property);
			subProp.mapperBuilder.addMapping(((SubPropertyMeta) property).getSubProperty(), columnName, column, sqlType);
		} else if (property.getType().isPrimitive()) {
			FieldMapper<ResultSet, T> fieldMapper = primitiveFieldMapperFactory.primitiveFieldMapper(column, property.getSetter(), columnName, fieldMapperErrorHandler);
			fields.add(fieldMapper);
		} else {
			FieldMapper<ResultSet, T> fieldMapper = objectFieldMapper(columnName, column, property.getSetter(), sqlType);
			fields.add(fieldMapper);
		}
		
	
	}

	@SuppressWarnings("unchecked")
	private FieldMapper<ResultSet, T> objectFieldMapper(String column, Setter<T, ?> setter, int sqlType) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory.newGetter(type, column, sqlType);
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("No getter for column " + column + " type " + type);
			return null;
		} else {
			return new FieldMapperImpl<ResultSet, T, Object>(column, getter, (Setter<T, Object>) setter, fieldMapperErrorHandler);
		}
	}

	@SuppressWarnings("unchecked")
	private FieldMapper<ResultSet, T> objectFieldMapper(String columnName, int column, Setter<T, ?> setter, int sqlType) {
		Class<? extends Object> type = setter.getPropertyType();
		Getter<ResultSet, ? extends Object> getter = ResultSetGetterFactory.newGetter(type, column, sqlType);
		if (getter == null) {
			mapperBuilderErrorHandler.getterNotFound("No getter for column " + columnName + " type " + type);
			return null;
		} else {
			return new FieldMapperImpl<ResultSet, T, Object>(columnName, getter, (Setter<T, Object>) setter,	fieldMapperErrorHandler);
		}
	}
	
	private static class SubProperty<T> {
		final ResultSetMapperBuilderImpl<?> mapperBuilder;
		final SubPropertyMeta<T, ?> subProperty;

		SubProperty(ResultSetMapperBuilderImpl<?> mapperBuilder,
				SubPropertyMeta<T, ?> subProperty) {
			this.mapperBuilder = mapperBuilder;
			this.subProperty = subProperty;
		}

	}

}