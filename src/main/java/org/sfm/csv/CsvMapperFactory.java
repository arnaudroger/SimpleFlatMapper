package org.sfm.csv;

import org.sfm.csv.impl.CellValueReaderFactoryImpl;
import org.sfm.csv.impl.CsvColumnDefinitionProviderImpl;
import org.sfm.csv.impl.DynamicCsvMapper;
import org.sfm.map.*;
import org.sfm.map.impl.*;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;

/**
 * CsvMapperFactory is not Thread-Safe but the mappers are.
 * It is strongly advised to instantiate one mapper per class for the life of your application.
 * <p>
 * You can instantiate dynamic mapper which will use the first line of the csv file
 * to figure out the list of the columns or a static one using a builder.
 * <p>
 * <code>
 *     // create a dynamic mapper targeting MyClass<br>
 *     CsvMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newMapper(MyClass.class);<br>
 *     <br>
 *     // create a static mapper targeting MyClass<br>
 *     CsvMapperFactory<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newInstance()<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.newBuilder(MyClass.class)<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("id")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("field1")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.addMapping("field2")<br>
 *     &nbsp;&nbsp;&nbsp;&nbsp;.mapper();<br>
 *     <br>
 * </code>
 */
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
	private RowHandlerErrorHandler rowHandlerErrorHandler = new RethrowRowHandlerErrorHandler();

	private CsvColumnDefinitionProviderImpl columnDefinitions = new CsvColumnDefinitionProviderImpl();

	private boolean useAsm = true;
	private boolean disableAsm = false;
    private boolean failOnAsm = false;
    private int asmMapperNbFieldsLimit = CsvMapperBuilder.NO_ASM_CSV_HANDLER_THRESHOLD;
	private int maxMethodSize = CsvMapperBuilder.CSV_MAX_METHOD_SIZE;

    private PropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory();
	
	private String defaultDateFormat = "yyyy-MM-dd HH:mm:ss";

	private CellValueReaderFactory cellValueReaderFactory = new CellValueReaderFactoryImpl();

	public CsvMapperFactory() {
	}

	/**
	 * Set a new FieldMapperErrorHandler. Use to handle thrown during the mapping of a field.
	 * @param fieldMapperErrorHandler the FieldMapperErrorHandler
	 * @return the current factory
	 */
	public CsvMapperFactory fieldMapperErrorHandler(final FieldMapperErrorHandler<CsvColumnKey> fieldMapperErrorHandler) {
		this.fieldMapperErrorHandler = fieldMapperErrorHandler;
		return this;
	}

	/**
	 * Set a new MapperBuilderErrorHandler. Use to handle to handle Mapper builder error when reading the header.
	 * @param mapperBuilderErrorHandler the MapperBuilderErrorHandler
	 * @return the current factory
	 */
	public CsvMapperFactory mapperBuilderErrorHandler(final MapperBuilderErrorHandler mapperBuilderErrorHandler) {
		this.mapperBuilderErrorHandler = mapperBuilderErrorHandler;
		return this;
	}

    /**
     * Set a new RowHandlerErrorHandler. Use to handle error thrown by the RowHandler on the forEach call.
     * @param rowHandlerErrorHandler the RowHandlerErrorHandler
     * @return the current factory
     */
	public CsvMapperFactory rowHandlerErrorHandler(final RowHandlerErrorHandler rowHandlerErrorHandler) {
		this.rowHandlerErrorHandler = rowHandlerErrorHandler;
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
     *
     * @param failOnAsm true if don't want to recover from an asm generation failure of the mapper
     * @return the factory
     */
    public CsvMapperFactory failOnAsm(final boolean failOnAsm) {
        this.failOnAsm = failOnAsm;
        return this;
    }

    /**
     * change the number of fields threshold after which an asm mapper is not generated.
     * <p>
     * the default value is calculated from the benchmark results, currently 240.
     * @param asmMapperNbFieldsLimit the limit after which it does not use asm for the mapper.
     * @return the factory
     */
    public CsvMapperFactory asmMapperNbFieldsLimit(final int asmMapperNbFieldsLimit) {
        this.asmMapperNbFieldsLimit = asmMapperNbFieldsLimit;
        return this;
    }

	/**
	 * Number needs to be a power of 2, do not use if you don't know what it does.
	 * @param maxMethodSize the max method size, needs be a power of 2.
	 * @return the factory.
	 */
	public CsvMapperFactory maxMethodSize(final int maxMethodSize) {
		this.maxMethodSize = maxMethodSize;
		return this;
	}
    /**
	 * @param disableAsm true if you want to disable asm.
     * @return the current factory
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
     * @param <T> the targeted type
	 * @return a jdbc mapper that will map to the targeted class.
	 * @throws MapperBuildingException if an error occurs building the mapper
	 */
	public <T> CsvMapper<T> newMapper(final Class<T> target) throws MapperBuildingException {
		return newMapper((Type)target);
	}

    public <T> CsvMapper<T> newMapper(final TypeReference<T> target) throws MapperBuildingException {
        return newMapper(target.getType());
    }

    public <T> CsvMapper<T> newMapper(final Type target) throws MapperBuildingException {
		ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicCsvMapper<T>(target,
				classMeta,
				fieldMapperErrorHandler, mapperBuilderErrorHandler,
				rowHandlerErrorHandler, defaultDateFormat, columnDefinitions,
                propertyNameMatcherFactory, cellValueReaderFactory,
				failOnAsm, asmMapperNbFieldsLimit, maxMethodSize);
	}

	private <T> ClassMeta<T> getClassMeta(Type target) {
		return ReflectionService.newInstance(disableAsm, useAsm).getClassMeta(target);
	}


	/**
	 * Will create a instance of ResultSetMapperBuilder 
	 * @param target the target class of the mapper
     * @param <T> the targeted type
	 * @return a builder ready to instantiate a mapper or to be customized
     * @throws MapperBuildingException if an error occurs building the mapper
	 */
	public <T> CsvMapperBuilder<T> newBuilder(final Class<T> target) {
		return newBuilder((Type)target);
	}

    public <T> CsvMapperBuilder<T> newBuilder(final TypeReference<T> target) {
        return newBuilder(target.getType());
    }

    public <T> CsvMapperBuilder<T> newBuilder(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);
		CsvMapperBuilder<T> builder = new CsvMapperBuilder<T>(target, classMeta,
                mapperBuilderErrorHandler, columnDefinitions,
                propertyNameMatcherFactory, cellValueReaderFactory,
                0, failOnAsm, asmMapperNbFieldsLimit, maxMethodSize);
		builder.fieldMapperErrorHandler(fieldMapperErrorHandler);
		builder.rowHandlerErrorHandler(rowHandlerErrorHandler);
		builder.setDefaultDateFormat(defaultDateFormat);
		return builder;
	}


	public CsvMapperFactory addAlias(String key, String value) {
		return addColumnDefinition(key, CsvColumnDefinition.renameDefinition(value));
	}

	public CsvMapperFactory addCustomValueReader(String key,	CellValueReader<?> cellValueReader) {
		return addColumnDefinition(key, CsvColumnDefinition.customReaderDefinition(cellValueReader));
	}

	public CsvMapperFactory propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		return this;
	}

	public CsvMapperFactory addColumnDefinition(String key, CsvColumnDefinition columnDefinition) {
		return addColumnDefinition(new CaseInsensitiveFieldKeyNamePredicate(key), columnDefinition);
	}

	public CsvMapperFactory addColumnDefinition(Predicate<? super CsvColumnKey> predicate, CsvColumnDefinition columnDefinition) {
		columnDefinitions.addColumnDefinition(predicate, columnDefinition);
		return this;
	}

    public CsvMapperFactory addKeys(String... columns) {
        for(String col : columns) {
            addColumnDefinition(col, CsvColumnDefinition.key());
        }
        return this;
    }
}
