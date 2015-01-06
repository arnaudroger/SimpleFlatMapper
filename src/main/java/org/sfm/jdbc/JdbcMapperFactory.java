package org.sfm.jdbc;

import org.sfm.jdbc.impl.DynamicJdbcMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.DefaultPropertyNameMatcherFactory;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.RethrowMapperBuilderErrorHandler;
import org.sfm.map.impl.RethrowRowHandlerErrorHandler;
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
	private Map<String, String> aliases = new HashMap<String, String>();
	private Map<String, FieldMapper<ResultSet, ?>> customMappings = new HashMap<String, FieldMapper<ResultSet, ?>>();

	private PropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory();
	
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
		ClassMeta<T> classMeta = getClassMeta(target);

		JdbcMapperBuilder<T> builder = new JdbcMapperBuilder<T>(target, classMeta, aliases, customMappings, propertyNameMatcherFactory);
		
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
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
		ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicJdbcMapper<T>(target, classMeta, fieldMapperErrorHandler, mapperBuilderErrorHandler, rowHandlerErrorHandler, aliases, customMappings, propertyNameMatcherFactory);
	}

	
	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public JdbcMapperFactory addAlias(String key, String value) {
		aliases.put(key.toUpperCase(), value.toUpperCase());
		return this;
	}

	/**
	 * 
	 * @param column
	 * @param fieldMapper
	 * @return
	 */
	public JdbcMapperFactory addCustomFieldMapper(String column, FieldMapper<ResultSet, ?> fieldMapper) {
		customMappings.put(column.toUpperCase(), fieldMapper);
		return this;
	}


	public JdbcMapperFactory propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		return this;
	}

	private <T> ClassMeta<T> getClassMeta(Type target) {
		return ReflectionService.newInstance(disableAsm, useAsm).getClassMeta(target);
	}

	public JdbcMapperFactory addAliases(Map<String, String> aliases) {
		this.aliases.putAll(aliases);
		return this;
	}
}
