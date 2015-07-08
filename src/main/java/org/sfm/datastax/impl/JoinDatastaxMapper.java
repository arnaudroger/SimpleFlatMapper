package org.sfm.datastax.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.sfm.datastax.DatastaxMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.AbstractEnumarableDelegateMapper;
import org.sfm.map.impl.JoinEnumarable;
import org.sfm.utils.Enumarable;


public final class JoinDatastaxMapper<T> extends AbstractEnumarableDelegateMapper<Row, ResultSet, T, DriverException> implements DatastaxMapper<T> {

    private final Mapper<Row, T> mapper;
    private final MappingContextFactory<? super Row> mappingContextFactory;

    public JoinDatastaxMapper(Mapper<Row, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<? super Row> mappingContextFactory) {
        super(errorHandler);
        this.mapper = mapper;
        this.mappingContextFactory = mappingContextFactory;
    }

    @Override
    protected Mapper<Row, T> getMapper(Row row) {
        return mapper;
    }

    @Override
    protected Enumarable<T> newEnumarableOfT(ResultSet rs)  {
        return new JoinEnumarable<Row, T>(mapper, mappingContextFactory.newContext(), new ResultSetEnumarable(rs));
    }
}
