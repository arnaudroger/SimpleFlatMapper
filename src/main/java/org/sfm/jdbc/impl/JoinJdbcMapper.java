package org.sfm.jdbc.impl;

import org.sfm.map.*;
import org.sfm.map.impl.JoinEnumarable;
import org.sfm.utils.Enumarable;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class JoinJdbcMapper<T> extends AbstractEnumarableJdbcMapper<T> {

    private final Mapper<ResultSet, T> mapper;
    private final MappingContextFactory<ResultSet> mappingContextFactory;

    public JoinJdbcMapper(Mapper<ResultSet, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<ResultSet> mappingContextFactory) {
        super(errorHandler);
        this.mapper = mapper;
        this.mappingContextFactory = mappingContextFactory;
    }

    @Override
    protected Mapper<ResultSet, T> getMapper(ResultSet source) {
        return mapper;
    }

    @Override
    protected Enumarable<T> newEnumarableOfT(ResultSet rs) throws SQLException {
        return new JoinEnumarable<ResultSet, T>(mapper, mappingContextFactory.newContext(), new ResultSetEnumarable(rs));
    }

    @Override
    public MappingContext<ResultSet> newMappingContext(ResultSet rs) {
        return mappingContextFactory.newContext();
    }
}
