package org.sfm.csv;

import org.sfm.csv.impl.CellValueReaderFactoryImpl;
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
	
	private Map<String, CsvColumnDefinition> columnDefinitions = new HashMap<String, CsvColumnDefinition>();

	private boolean useAsm = true;
	private boolean disableAsm = false;
	
	private PropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory();
	
	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

	private CellValueReaderFactory cellValueReaderFactory = new CellValueReaderFactoryImpl();

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

	public CsvMapperFactory cellValueReaderFactory(final CellValueReaderFactory cellValueReaderFactory) {
		this.cellValueReaderFactory = cellValueReaderFactory;
		return this;
	}


	/**
	 * 
	 * @param target the targeted class for the mapper
	 * @return a jdbc mapper that will map to the targeted class.
	 * @throws MapperBuildingException
	 */
	public <T> CsvMapper<T> newMapper(final Class<T> target) throws MapperBuildingException {
		return newMapper((Type)target);
	}

	public <T> CsvMapper<T> newMapper(final Type target) throws MapperBuildingException {
		ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicCsvMapper<T>(target,
				classMeta,
				fieldMapperErrorHandler, mapperBuilderErrorHandler,
				defaultDateFormat, columnDefinitions, propertyNameMatcherFactory, cellValueReaderFactory);
	}

	private <T> ClassMeta<T> getClassMeta(Type target) {
		return ReflectionService.newInstance(disableAsm, useAsm).getRootClassMeta(target);
	}


	/**
	 * Will create a instance of ResultSetMapperBuilder 
	 * @param target the target class of the mapper
	 * @return a builder ready to instantiate a mapper or to be customized
	 * @throws MapperBuildingException
	 */
	public <T> CsvMapperBuilder<T> newBuilder(final Class<T> target) {
		return newBuilder((Type)target);
	}

	public <T> CsvMapperBuilder<T> newBuilder(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, classMeta, mapperBuilderErrorHandler, columnDefinitions, propertyNameMatcherFactory, cellValueReaderFactory);
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.setDefaultDateFormat(defaultDateFormat);
		return builder;
	}


	public CsvMapperFactory addAlias(String key, String value) {
		CsvColumnDefinition columnDefinition = getColumnDefinition(key);
		columnDefinitions.put(key.toLowerCase(), CsvColumnDefinition.compose(columnDefinition, CsvColumnDefinition.renameDefinition(value)));
		return this;
	}

	public CsvMapperFactory addCustomValueReader(String key,	CellValueReader<?> cellValueReader) {
		CsvColumnDefinition columnDefinition = getColumnDefinition(key);
		columnDefinitions.put(key.toLowerCase(), CsvColumnDefinition.compose(columnDefinition, CsvColumnDefinition.customReaderDefinition(cellValueReader)));
		return this;
	}

	private CsvColumnDefinition getColumnDefinition(String key) {
		CsvColumnDefinition columnDefinition = columnDefinitions.get(key.toLowerCase());
		if (columnDefinition == null) {
			return CsvColumnDefinition.IDENTITY;
		}
		return columnDefinition;
	}

	public CsvMapperFactory propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		return this;
	}

	public CsvMapperFactory addColumnDefinition(String key, CsvColumnDefinition columnDefinition) {
		CsvColumnDefinition cd = getColumnDefinition(key);
		columnDefinitions.put(key.toLowerCase(), CsvColumnDefinition.compose(cd, columnDefinition));
		return this;
	}
}
