package org.simpleflatmapper.sql2o;

import org.simpleflatmapper.jdbc.DynamicJdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.mapper.DefaultPropertyNameMatcherFactory;
import org.sql2o.DefaultResultSetHandlerFactoryBuilder;
import org.sql2o.ResultSetHandlerFactory;

import java.util.Map;

public class SfmResultSetHandlerFactoryBuilder extends DefaultResultSetHandlerFactoryBuilder {

    public SfmResultSetHandlerFactoryBuilder() {
        super();
    }

    @Override
    public <E> ResultSetHandlerFactory<E> newFactory(Class<E> aClass) {

        boolean exactMatch = !isAutoDeriveColumnNames();

        DefaultPropertyNameMatcherFactory propertyNameMatcherFactory = DefaultPropertyNameMatcherFactory.DEFAULT.exactMatch(exactMatch).caseSensitive(isCaseSensitive());
        Map<String, String> columnMappings = getColumnMappings();

        JdbcMapperFactory jdbcMapperFactory = JdbcMapperFactory
                .newInstance()
                .propertyNameMatcherFactory(propertyNameMatcherFactory);
        if (columnMappings != null) {
            jdbcMapperFactory.addAliases(columnMappings);
        }

        DynamicJdbcMapper<E> dynamicJdbcMapper = (DynamicJdbcMapper<E>)jdbcMapperFactory.newMapper(aClass);
        return new SfmResultSetHandlerFactory<E>(dynamicJdbcMapper);
    }
}
