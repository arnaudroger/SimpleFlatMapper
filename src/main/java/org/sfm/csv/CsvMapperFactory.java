package org.sfm.csv;

import org.sfm.jdbc.AsmHelper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.RethrowFieldMapperErrorHandler;
import org.sfm.map.RethrowMapperBuilderErrorHandler;
import org.sfm.osgi.BridgeClassLoader;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.asm.AsmFactory;

public final class CsvMapperFactory {
	
	private static final AsmFactory _asmFactory = AsmHelper.isAsmPresent() ? new AsmFactory() : null;
	
	/**
	 * instantiate a new JdbcMapperFactory
	 * @return a new JdbcMapperFactory
	 */
	public static CsvMapperFactory newInstance() {
		return new CsvMapperFactory();
	}
	
	private FieldMapperErrorHandler<Integer> fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler<Integer>();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	
	private boolean useAsm = true;
	private boolean disableAsm = false;
	
	private final boolean useBridgeClassLoader;
	
	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";
	
	public CsvMapperFactory(boolean useBridgeClassLoader) {
		this.useBridgeClassLoader = useBridgeClassLoader;
	}
	
	public CsvMapperFactory() {
		this(false);
	}
	
	/**
	 * 
	 * @param fieldMapperErrorHandler 
	 * @return the factory
	 */
	public CsvMapperFactory fieldMapperErrorHandler(final FieldMapperErrorHandler<Integer> fieldMapperErrorHandler) {
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		return this;
	}

	/**
	 * 
	 * @param mapperBuilderErrorHandler
	 * @return the factory
	 */
	public CsvMapperFactory mapperBuilderErrorHandler(final MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		return this;
	}

	/**
	 * 
	 * @param useAsm false if you want to disable asm usage.
	 * @return the factory
	 */
	public CsvMapperFactory useAsm(final boolean useAsm) {
		this.useAsm = useAsm;
		return this;
	}
	
	/**
	 * @param disableAsm true if you want to disable asm.
	 */
	public CsvMapperFactory disableAsm(final boolean disableAsm) {
		this.disableAsm = disableAsm;
		return this;
	}
	
	public CsvMapperFactory defaultDateFormat(final String defaultDateFormat) {
		this.defaultDateFormat = defaultDateFormat;
		return this;
	}
	/**
	 * 
	 * @param target the targeted class for the mapper
	 * @return a jdbc mapper that will map to the targeted class.
	 * @throws MapperBuildingException
	 */
	public <T> CsvMapper<T> newMapper(final Class<T> target) throws MapperBuildingException {
		return new DynamicCsvMapper<T>(target, reflectionService(target), fieldMapperErrorHandler, mapperBuilderErrorHandler, defaultDateFormat);
	}

	
	/**
	 * Will create a instance of ResultSetMapperBuilder 
	 * @param target the target class of the mapper
	 * @return a builder ready to instantiate a mapper or to be customized
	 * @throws MapperBuildingException
	 */
	public <T> CsvMapperBuilder<T> newBuilder(final Class<T> target) {
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, reflectionService(target));
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		builder.setDefaultDateFormat(defaultDateFormat);
		return builder;
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
}
