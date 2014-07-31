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

public class JdbcMapperFactory {
	
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
	
	private final boolean asmPresent = isAsmPresent();
	
	
	/**
	 * Will create a instance of mapper based on the metadata and the target class;
	 * @param target the target class of the mapper
	 * @param metaData the metadata to create the mapper from
	 * @return a mapper that will map the data represented by the metadata to an instance of target
	 * @throws SQLException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public <T> JdbcMapper<T> newMapper(Class<T> target, ResultSetMetaData metaData) throws SQLException, NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<T> builder = new ResultSetMapperBuilder<>(target, getSetterFactory());
		
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		
		for(int i = 0; i < metaData.getColumnCount(); i++) {
			builder.addIndexedColumn(metaData.getColumnName(i +1));
		}
		
		return new DelegateJdbcMapper<T>(builder.mapper(), new InstantiatorFactory(getAsmSetterFactory()).getInstantiator(target));
	}
	
	private boolean isAsmPresent() {
		try {
			Class.forName("org.objectweb.asm.Opcodes");
			return true;
		} catch(Exception e) {
			return false;
		}
	}

	
	/**
	 * 
	 * @param fieldMapperErrorHandler 
	 * @return the factory
	 */
	public JdbcMapperFactory fieldMapperErrorHandler(
			FieldMapperErrorHandler fieldMapperErrorHandler) {
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		return this;
	}

	/**
	 * 
	 * @param mapperBuilderErrorHandler
	 * @return the factory
	 */
	public JdbcMapperFactory mapperBuilderErrorHandler(
			MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		return this;
	}

	/**
	 * 
	 * @param useAsm false if you want to disable asm usage.
	 * @return the factory
	 */
	public JdbcMapperFactory useAsm(boolean useAsm) {
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
	public <T> JdbcMapper<T> newMapper(Class<T> target) throws NoSuchMethodException, SecurityException {
		return new DynamicJdbcMapper<T>(target, getSetterFactory(), new InstantiatorFactory(getAsmSetterFactory()).getInstantiator(target), fieldMapperErrorHandler, mapperBuilderErrorHandler);
	}

	private SetterFactory getSetterFactory() {
		return new SetterFactory(getAsmSetterFactory());
	}

	private AsmFactory getAsmSetterFactory() {
		return useAsm && asmPresent ? new AsmFactory() : null;
	}
}
