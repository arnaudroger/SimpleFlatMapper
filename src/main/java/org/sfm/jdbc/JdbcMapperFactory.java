package org.sfm.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.asm.AsmFactory;

public final class JdbcMapperFactory {
	
	/**
	 * instantiate a new JdbcMapperFactory
	 * @return a new JdbcMapperFactory
	 */
	public static JdbcMapperFactory newInstance() {
		return new JdbcMapperFactory();
	}
	
	private FieldMapperErrorHandler fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	
	private boolean useAsm = true;
	
	
	
	/**
	 * Will create a instance of mapper based on the metadata and the target class;
	 * @param target the target class of the mapper
	 * @param metaData the metadata to create the mapper from
	 * @return a mapper that will map the data represented by the metadata to an instance of target
	 * @throws SQLException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target, final ResultSetMetaData metaData) throws SQLException, NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<T> builder = new ResultSetMapperBuilderImpl<>(target, getSetterFactory());
		
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		builder.addMapping(metaData);
		
		return new DelegateJdbcMapper<T>(builder.mapper(), new InstantiatorFactory(getAsmSetterFactory()).getInstantiator(target));
	}
	
	/**
	 * 
	 * @param fieldMapperErrorHandler 
	 * @return the factory
	 */
	public JdbcMapperFactory fieldMapperErrorHandler(final FieldMapperErrorHandler fieldMapperErrorHandler) {
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
	 * 
	 * @param target the targeted class for the mapper
	 * @return a jdbc mapper that will map to the targeted class.
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public <T> JdbcMapper<T> newMapper(final Class<T> target) throws NoSuchMethodException, SecurityException {
		return new DynamicJdbcMapper<T>(target, getSetterFactory(), new InstantiatorFactory(getAsmSetterFactory()).getInstantiator(target), fieldMapperErrorHandler, mapperBuilderErrorHandler);
	}

	private AsmFactory getAsmSetterFactory() {
		return !useAsm ? null : AsmHelper.getAsmSetterFactory();
	}

	private SetterFactory getSetterFactory() {
		return new SetterFactory(getAsmSetterFactory());
	}


}
