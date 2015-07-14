package org.sfm.sql2o;

import org.sfm.jdbc.DynamicJdbcMapper;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.map.impl.DefaultPropertyNameMatcherFactory;
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

        DefaultPropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory(exactMatch, isCaseSensitive());
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
