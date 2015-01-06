package org.sfm.sql2o;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.jdbc.JdbcMapperFactory;
import org.sfm.jdbc.impl.DynamicJdbcMapper;
import org.sql2o.DefaultResultSetHandlerFactoryBuilder;
import org.sql2o.ResultSetHandlerFactory;

public class SfmResultSetHandlerFactoryBuilder extends DefaultResultSetHandlerFactoryBuilder {

    @Override
    public <E> ResultSetHandlerFactory<E> newFactory(Class<E> aClass) {
        return new SfmResultSetHandlerFactory<E>((DynamicJdbcMapper<E>) JdbcMapperFactory.newInstance().newMapper(aClass));
    }
}
