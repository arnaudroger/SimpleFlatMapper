package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.*;
import org.sfm.map.impl.JoinEnumarable;
import org.sfm.utils.Enumarable;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class JoinJdbcMapper<T> extends AbstractEnumarableJdbcMapper<T> {

    private final JdbcMapper<T> mapper;

    public JoinJdbcMapper(JdbcMapper<T> mapper, RowHandlerErrorHandler errorHandler) {
        super(errorHandler);
        this.mapper = mapper;
    }

    @Override
    protected JdbcMapper<T> getMapper(ResultSet source) {
        return mapper;
    }

    @Override
    protected Enumarable<T> newEnumarableOfT(ResultSet rs) throws SQLException {
        return new JoinEnumarable<ResultSet, T>(mapper, newMappingContext(rs), new ResultSetEnumarable(rs));
    }

}
