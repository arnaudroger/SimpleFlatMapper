package org.sfm.csv;

import org.sfm.csv.impl.DynamicCsvMapper;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.map.impl.DefaultPropertyNameMatcherFactory;
import org.sfm.map.impl.RethrowFieldMapperErrorHandler;
import org.sfm.map.impl.RethrowMapperBuilderErrorHandler;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class CsvMapperFactory {
	

	/**
	 * instantiate a new JdbcMapperFactory
	 * @return a new JdbcMapperFactory
	 */
	public static CsvMapperFactory newInstance() {
		return new CsvMapperFactory();
	}
	
	private FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler = new RethrowFieldMapperErrorHandler<CsvColumnKey>();
	private MapperBuilderErrorHandler mapperBuilderErrorHandler = new RethrowMapperBuilderErrorHandler();
	
	private Map<String, String> aliases = new HashMap<String, String>();
	private Map<String, CellValueReader<?>> customReaders = new HashMap<String, CellValueReader<?>>();
	
	private boolean useAsm = true;
	private boolean disableAsm = false;
	
	private PropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory();
	
	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";
	

	public CsvMapperFactory() {
	}

	/**
	 * 
	 * @param fieldMapperErrorHandler 
	 * @return the factory
	 */
	public CsvMapperFactory fieldMapperErrorHandler(final FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler) {
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
	public <T> CsvMapper<T> newMapper(final Type target) throws MapperBuildingException {
		ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicCsvMapper<T>(target,
				classMeta,
				fieldMapperErrorHandler, mapperBuilderErrorHandler,
				defaultDateFormat, aliases, customReaders, propertyNameMatcherFactory);
	}

	private <T> ClassMeta<T> getClassMeta(Type target) {
		return ReflectionService.newInstance(disableAsm, useAsm).getClassMeta(target);
	}


	/**
	 * Will create a instance of ResultSetMapperBuilder 
	 * @param target the target class of the mapper
	 * @return a builder ready to instantiate a mapper or to be customized
	 * @throws MapperBuildingException
	 */
	public <T> CsvMapperBuilder<T> newBuilder(final Class<T> target) {
		ClassMeta<T> classMeta = getClassMeta(target);
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, classMeta, aliases, customReaders, propertyNameMatcherFactory);
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.mapperBuilderErrorHandler(mapperBuilderErrorHandler);
		builder.setDefaultDateFormat(defaultDateFormat);
		return builder;
	}


	public CsvMapperFactory addAlias(String key, String value) {
		aliases.put(key.toUpperCase(), value.toUpperCase());
		return this;
	}

	public CsvMapperFactory addCustomValueReader(String column,	CellValueReader<?> cellValueReader) {
		customReaders.put(column, cellValueReader);
		return this;
	}

	public CsvMapperFactory propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		return this;
	}
}
