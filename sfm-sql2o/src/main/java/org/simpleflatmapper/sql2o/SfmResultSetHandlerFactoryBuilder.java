package org.simpleflatmapper.sql2o;

import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.PropertyNameMatcherFactory;
import org.simpleflatmapper.map.mapper.DefaultPropertyNameMatcherFactory;
import org.simpleflatmapper.util.Function;
import org.sql2o.DefaultResultSetHandlerFactoryBuilder;
import org.sql2o.ResultSetHandlerFactory;

import java.util.Map;

public class SfmResultSetHandlerFactoryBuilder extends DefaultResultSetHandlerFactoryBuilder {


    private final Function<Class<?>, JdbcMapperFactory> jdbcMapperFactoryFactory;

    public SfmResultSetHandlerFactoryBuilder() {
        this(JdbcMapperFactory.newInstance());
    }

    public SfmResultSetHandlerFactoryBuilder(final JdbcMapperFactory jdbcMapperFactory) {
        this(new Function<Class<?>, JdbcMapperFactory>() {
            @Override
            public JdbcMapperFactory apply(Class<?> aClass) {
                return jdbcMapperFactory;
            }
        });
    }

    public SfmResultSetHandlerFactoryBuilder(Function<Class<?>, JdbcMapperFactory> jdbcMapperFactoryFactory) {
        this.jdbcMapperFactoryFactory = jdbcMapperFactoryFactory;
    }

    @Override
    public <E> ResultSetHandlerFactory<E> newFactory(Class<E> aClass) {

        boolean exactMatch = !isAutoDeriveColumnNames();

        JdbcMapperFactory jdbcMapperFactory = jdbcMapperFactoryFactory.apply(aClass);

        PropertyNameMatcherFactory propertyNameMatcherFactory =
                jdbcMapperFactory.mapperConfig(aClass).propertyNameMatcherFactory();
        if (propertyNameMatcherFactory instanceof DefaultPropertyNameMatcherFactory) {
            propertyNameMatcherFactory  =
                    ((DefaultPropertyNameMatcherFactory)propertyNameMatcherFactory).exactMatch(exactMatch).caseSensitive(isCaseSensitive());
        }
        jdbcMapperFactory.propertyNameMatcherFactory(propertyNameMatcherFactory);

        Map<String, String> columnMappings = getColumnMappings();
        if (columnMappings != null) {
            jdbcMapperFactory.addAliases(columnMappings);
        }

        DynamicJdbcMapper<E> dynamicJdbcMapper = (DynamicJdbcMapper<E>) jdbcMapperFactory.newMapper(aClass);

        return new SfmResultSetHandlerFactory<E>(dynamicJdbcMapper);
    }
}
