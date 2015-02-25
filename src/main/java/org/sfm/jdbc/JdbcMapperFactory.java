package org.sfm.jdbc;

import org.sfm.jdbc.impl.DynamicJdbcMapper;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.*;
import org.sfm.map.impl.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeReference;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.PropertyNameMatcherFactory;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
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
    private FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey, ResultSet> columnDefinitions = new FieldMapperColumnDefinitionProviderImpl<JdbcColumnKey, ResultSet>();
	private PropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory();

    private GetterFactory<ResultSet, JdbcColumnKey> getterFactory = new ResultSetGetterFactory();
	private boolean useAsm = true;

    private boolean disableAsm = false;
    private boolean failOnAsm = false;

    private ReflectionService reflectionService = null;



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

    public JdbcMapperFactory reflectionService(final ReflectionService reflectionService) {
        this.reflectionService = reflectionService;
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

    public <T> JdbcMapperBuilder<T> newBuilder(final TypeReference<T> target) {
        return newBuilder(target.getType());
    }

    public <T> JdbcMapperBuilder<T> newBuilder(final Type target) {
		ClassMeta<T> classMeta = getClassMeta(target);

		JdbcMapperBuilder<T> builder = new JdbcMapperBuilder<T>(classMeta, mapperBuilderErrorHandler, columnDefinitions, propertyNameMatcherFactory, getterFactory, failOnAsm, new JdbcMappingContextFactoryBuilder());
		
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

    public <T> JdbcMapper<T> newMapper(final TypeReference<T> target) throws MapperBuildingException {
        return newMapper(target.getType());
    }

	public <T> JdbcMapper<T> newMapper(final Type target) throws MapperBuildingException {
		ClassMeta<T> classMeta = getClassMeta(target);
		return new DynamicJdbcMapper<T>(classMeta, fieldMapperErrorHandler, mapperBuilderErrorHandler, rowHandlerErrorHandler, columnDefinitions, propertyNameMatcherFactory, failOnAsm);
	}


	/**
	 * 
	 * @param key
	 * @param value
	 * @return
	 */
	public JdbcMapperFactory addAlias(String key, String value) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>renameDefinition(value));
	}

	/**
	 * 
	 * @param key
	 * @param fieldMapper
	 * @return
	 */
	public JdbcMapperFactory addCustomFieldMapper(String key, FieldMapper<ResultSet, ?> fieldMapper) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>customFieldMapperDefinition(fieldMapper));
	}

	public JdbcMapperFactory addCustomGetter(String key, Getter<ResultSet, Long> getter) {
		return addColumnDefinition(key, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>customGetter(getter));
	}

	public JdbcMapperFactory addColumnDefinition(String key, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
		return addColumnDefinition(new CaseInsensitiveFieldKeyNamePredicate(key), columnDefinition);
	}

	public JdbcMapperFactory addColumnDefinition(Predicate<? super JdbcColumnKey> predicate, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> columnDefinition) {
		columnDefinitions.addColumnDefinition(predicate, columnDefinition);
		return this;
	}

	public JdbcMapperFactory propertyNameMatcherFactory(PropertyNameMatcherFactory propertyNameMatcherFactory) {
		this.propertyNameMatcherFactory = propertyNameMatcherFactory;
		return this;
	}

	private <T> ClassMeta<T> getClassMeta(Type target) {
		return getReflectionService().getRootClassMeta(target);
	}

    private ReflectionService getReflectionService() {
        if (reflectionService != null) {
            return reflectionService;
        } else {
            return ReflectionService.newInstance(disableAsm, useAsm);
        }
    }

    public JdbcMapperFactory addAliases(Map<String, String> aliases) {
		for(Map.Entry<String, String> e : aliases.entrySet()) {
			addAlias(e.getKey(), e.getValue());
		}
		return this;
	}

    public JdbcMapperFactory failOnAsm(boolean b) {
        this.failOnAsm = b;
        return this;
    }

    /**
     * Define keys use to detect when to break the 1-n join.
     * @param columns name of the column to define as key
     * @return this
     */
    public JdbcMapperFactory addKeys(String... columns) {
        for(String col : columns) {
            addColumnDefinition(col, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>key());
        }
        return this;
    }

    public <T> JdbcDiscriminatorBuilder<T> newDiscriminator(String column, Type root) {
        addColumnDefinition(column, FieldMapperColumnDefinition.<JdbcColumnKey, ResultSet>ignoreDefinition());
        return new JdbcDiscriminatorBuilder<T>(column, root, this);
    }

    public <T> JdbcDiscriminatorBuilder<T> newDiscriminator(String column, Class<T> root) {
        return newDiscriminator(column, (Type)root);
    }

    public <T> JdbcDiscriminatorBuilder<T> newDiscriminator(String column, TypeReference<T> root) {
        return newDiscriminator(column, root.getType());
    }

    public RowHandlerErrorHandler rowHandlerErrorHandler() {
        return rowHandlerErrorHandler;
    }
}
