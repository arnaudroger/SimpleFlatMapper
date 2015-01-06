package org.sfm.sql2o;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.impl.DynamicJdbcMapper;
import org.sfm.map.impl.DefaultPropertyNameMatcherFactory;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sql2o.DefaultResultSetHandlerFactoryBuilder;
import org.sql2o.ResultSetHandlerFactory;

import java.util.Map;

public class SfmResultSetHandlerFactoryBuilder extends DefaultResultSetHandlerFactoryBuilder {

    @Override
    public <E> ResultSetHandlerFactory<E> newFactory(Class<E> aClass) {

        DefaultPropertyNameMatcherFactory propertyNameMatcherFactory = new DefaultPropertyNameMatcherFactory(!isAutoDeriveColumnNames(), isCaseSensitive());
        Map<String, String> columnMappings = getColumnMappings();

        DynamicJdbcMapper<E> dynamicJdbcMapper = (DynamicJdbcMapper<E>)
                JdbcMapperFactory
                        .newInstance()
                        .propertyNameMatcherFactory(propertyNameMatcherFactory)
                        .addAliases(columnMappings)
                        .newMapper(aClass);
        return new SfmResultSetHandlerFactory<E>(dynamicJdbcMapper);
    }
}
