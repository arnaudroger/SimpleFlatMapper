package org.sfm.jdbc;

import org.sfm.jdbc.impl.DynamicJdbcMapper;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.*;
import org.sfm.map.impl.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public final class JdbcMapperFactory {


	/**
	 * instantiate a new JdbcMapperFactory
	 * @return a new JdbcMapperFactory
	 */
	public static JdbcMapperFactory newInstance() {
		return new JdbcMapperFactory();
	}
	
	private FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler = null;
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private RowHandlerErrorHandler rowHandlerErrorHandler = new RethrowRowHandlerErrorHandler();
	private Map<String, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> columnDefinitions = new HashMap<String, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>>();

	private PropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory();
	private GetterFactory<ResultSet, JdbcColumnKey> getterFactory = new ResultSetGetterFactory();

	private boolean useAsm = true;
	private boolean disableAsm = false;
	

	public JdbcMapperFactory() {
	}
	
	/**
	 * 
	 * @param fieldMapperErrorHandler 
	 * @return the factory
	 */
	public JdbcMapperFactory fieldMapperErrorHandler(final FieldMapperErrorHandler<JdbcColumnKey> fieldMapperErrorHandler) {
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		return this;
	}

	/**
	 * 
	 * @param mapperBuilderErrorHandler
	 * @return the factory
	 */
	public JdbcMapperFactory mapperBuilderErrorHandler(final MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		return this;
	}


	public JdbcMapperFactory rowHandlerErrorHandler(final RowHandlerErrorHandler rowHandlerErrorHandler) {
		this.rowHandlerErrorHandler = rowHandlerErrorHandler;
		return this;
	}
	/**
	 * 
	 * @param useAsm false if you want to disable asm usage.
	 * @return the factory
	 */
	public JdbcMapperFactory useAsm(final boolean useAsm) {
		this.useAsm = useAsm;
		return this;
	}
	
	/**
	 * @param disableAsm true if you want to disable asm.
	 */
	public JdbcMapperFactory disableAsm(final boolean disableAsm) {
		this.disableAsm = disableAsm;
		return this;
	}


	public JdbcMapperFactory getterFactory(final GetterFactory<ResultSet, JdbcColumnKey> getterFactory) {
		this.getterFactory = getterFactory;
		return this;
	}


	/**
	 * Will create a instance of mapper based on the metadata and the target class;
	 * @param target the target class of the mapper
	 * @param metaData the metadata to create the mapper from
	 * @return a mapper that will map the data represented by the metadata to an instance of target
	 * @throws MapperBuildingException
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target, final ResultSetMetaData metaData) throws MapperBuildingException, SQLException {
		JdbcMapperBuilder<T> builder = newBuilder(target);
		builder.addMapping(metaData);
		return builder.mapper();
	}
	
	/**
	 * Will create a instance of ResultSetMapperBuilder 
	 * @param target the target class of the mapper
	 * @return a builder ready to instantiate a mapper or to be customized
	 * @throws MapperBuildingException
	 */
	public <T> JdbcMapperBuilder<T> newBuilder(final Class<T> target) {
		return newBuilder((Type)target);
	}

	public <T> JdbcMapperBuilder<T> newBuilder(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);

		JdbcMapperBuilder<T> builder = new JdbcMapperBuilder<T>(classMeta, mapperBuilderErrorHandler, columnDefinitions, propertyNameMatcherFactory, getterFactory);
		
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.jdbcMapperErrorHandler(rowHandlerErrorHandler);
		return builder;
	}

	/**
	 * 
	 * @param target the targeted class for the mapper
	 * @return a jdbc mapper that will map to the targeted class.
	 * @throws MapperBuildingException
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target) throws MapperBuildingException {
		return newMapper((Type)target);
	}

	public <T> JdbcMapper<T> newMapper(final Type target) throws MapperBuildingException {
		ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicJdbcMapper<T>(target, classMeta, fieldMapperErrorHandler, mapperBuilderErrorHandler, rowHandlerErrorHandler, columnDefinitions, propertyNameMatcherFactory);
	}


	private FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> getColumnDefinition(String key) {
		FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition = columnDefinitions.get(key.toLowerCase());
		if (columnDefinition == null) {
			columnDefinition = FieldMapperColumnDefinition.identity();
		}
		return columnDefinition;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public JdbcMapperFactory addAlias(String key, String value) {
		columnDefinitions.put(key.toLowerCase(), FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>compose(getColumnDefinition(key), FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>renameDefinition(value)));
		return this;
	}

	/**
	 * 
	 * @param key
	 * @param fieldMapper
	 * @return
	 */
	public JdbcMapperFactory addCustomFieldMapper(String key, FieldMapper<ResultSet, ?> fieldMapper) {
		columnDefinitions.put(key.toLowerCase(), FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>compose(getColumnDefinition(key), FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>customFieldMapperDefinition(fieldMapper)));
		return this;
	}

	public JdbcMapperFactory addCustomGetter(String key, Getter<ResultSet, Long> getter) {
		columnDefinitions.put(key.toLowerCase(), FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>compose(getColumnDefinition(key), FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>customGetter(getter)));
		return this;
	}

	public JdbcMapperFactory propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		return this;
	}

	private <T> ClassMeta<T> getClassMeta(Type target) {
		return ReflectionService.newInstance(disableAsm, useAsm).getRootClassMeta(target);
	}

	public JdbcMapperFactory addAliases(Map<String, String> aliases) {
		for(Map.Entry<String, String> e : aliases.entrySet()) {
			addAlias(e.getKey(), e.getValue());
		}
		return this;
	}


}
