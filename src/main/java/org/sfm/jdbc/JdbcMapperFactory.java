package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.sfm.map.FieldMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.osgi.BridgeClassLoader;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.asm.AsmFactory;

public final class JdbcMapperFactory {
	
	private static final AsmFactory _asmFactory = AsmHelper.isAsmPresent() ? new AsmFactory() : null;
	
	/**
	 * instantiate a new JdbcMapperFactory
	 * @return a new JdbcMapperFactory
	 */
	public static JdbcMapperFactory newInstance() {
		return new JdbcMapperFactory();
	}
	
	private FieldMapperErrorHandler<ColumnKey> fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler<ColumnKey>();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	private Map<String, String> aliases = new HashMap<String, String>();
	private Map<String, FieldMapper<ResultSet, ?>> customMappings = new HashMap<String, FieldMapper<ResultSet, ?>>();
	
	private boolean useAsm = true;
	private boolean disableAsm = false;
	
	private final boolean useBridgeClassLoader;
	
	
	public JdbcMapperFactory(boolean useBridgeClassLoader) {
		this.useBridgeClassLoader = useBridgeClassLoader;
	}
	
	public JdbcMapperFactory() {
		this(false);
	}
	
	/**
	 * 
	 * @param fieldMapperErrorHandler 
	 * @return the factory
	 */
	public JdbcMapperFactory fieldMapperErrorHandler(final FieldMapperErrorHandler<ColumnKey> fieldMapperErrorHandler) {
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
		ResultSetMapperBuilder<T> builder = newBuilder(target);
		builder.addMapping(metaData);
		return builder.mapper();
	}
	
	/**
	 * Will create a instance of ResultSetMapperBuilder 
	 * @param target the target class of the mapper
	 * @return a builder ready to instantiate a mapper or to be customized
	 * @throws MapperBuildingException
	 */
	public <T> ResultSetMapperBuilder<T> newBuilder(final Class<T> target) {
		ResultSetMapperBuilder<T> builder = new ResultSetMapperBuilderImpl<T>(target, reflectionService(target), aliases, customMappings);
		
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		return builder;
	}

	/**
	 * 
	 * @param target the targeted class for the mapper
	 * @return a jdbc mapper that will map to the targeted class.
	 * @throws MapperBuildingException
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target) throws MapperBuildingException {
		return new DynamicJdbcMapper<T>(target, reflectionService(target), fieldMapperErrorHandler, mapperBuilderErrorHandler, aliases, customMappings);
	}

	
	private ReflectionService reflectionService(Class<?> target) {
		AsmFactory asmFactory = null;
		if (AsmHelper.isAsmPresent() && !disableAsm) {
			if (useBridgeClassLoader) {
				asmFactory = new AsmFactory(new BridgeClassLoader(getClass().getClassLoader(), target.getClassLoader()));
			} else {
				asmFactory = _asmFactory;
			}
		}
		return new ReflectionService(AsmHelper.isAsmPresent() && !disableAsm, useAsm, asmFactory);
	}

	public void addAlias(String key, String value) {
		aliases.put(key.toUpperCase(), value.toUpperCase());
	}

	public void addCustomFieldMapper(String column,	FieldMapper<ResultSet, ?> fieldMapper) {
		customMappings.put(column.toUpperCase(), fieldMapper);
	}
}
