package org.sfm.jdbc;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.reflect.InstantiatorFactory;
import org.sfm.reflect.SetterFactory;
import org.sfm.reflect.asm.AsmSetterFactory;

public class JdbcMapperFactory {
	public static JdbcMapperFactory newInstance() {
		return new JdbcMapperFactory();
	}
	
	private FieldMapperErrorHandler fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	
	private boolean useSingleton;
	private boolean useAsm;
	
	private final boolean asmPresent = isAsmPresent();
	
	public <T> JdbcMapper<T> newMapper(Class<T> target, ResultSetMetaData metaData) throws SQLException, NoSuchMethodException, SecurityException {
		ResultSetMapperBuilder<T> builder = new ResultSetMapperBuilder<>(target, getSetterFactory());
		
		for(int i = 0; i < metaData.getColumnCount(); i++) {
			builder.addIndexedColumn(metaData.getColumnName(i +1));
		}
		
		return new DelegateJdbcMapper<T>(builder.mapper(), new InstantiatorFactory().getInstantiator(target), useSingleton);
	}
	
	private boolean isAsmPresent() {
		try {
			Class.forName("org.objectweb.asm.Opcodes");
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	

	public JdbcMapperFactory fieldMapperErrorHandler(
			FieldMapperErrorHandler fieldMapperErrorHandler) {
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		return this;
	}

	public JdbcMapperFactory mapperBuilderErrorHandler(
			MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		return this;
	}

	public JdbcMapperFactory useSingleton(boolean useSingleton) {
		this.useSingleton = useSingleton;
		return this;
	}

	public JdbcMapperFactory useAsm(boolean useAsm) {
		this.useAsm = useAsm;
		return this;
	}

	public <T> JdbcMapper<T> newMapper(Class<T> target) throws SQLException, NoSuchMethodException, SecurityException {
		return new DynamicJdbcMapper<T>(target, getSetterFactory(), new InstantiatorFactory().getInstantiator(target), fieldMapperErrorHandler, mapperBuilderErrorHandler, useSingleton);
	}

	private SetterFactory getSetterFactory() {
		return new SetterFactory(getAsmSetterFactory());
	}

	private AsmSetterFactory getAsmSetterFactory() {
		return useAsm && asmPresent ? new AsmSetterFactory() : null;
	}
}
