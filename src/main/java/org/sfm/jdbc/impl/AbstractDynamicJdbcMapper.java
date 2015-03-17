package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;
import org.sfm.utils.ErrorHelper;

import java.sql.ResultSet;
import java.sql.SQLException;


public abstract class AbstractDynamicJdbcMapper<T> implements JdbcMapper<T> {

    @Override
    public final T map(ResultSet source) throws MappingException {
        return map(source, null);
    }

    @Override
    public final T map(ResultSet source, MappingContext<ResultSet> mappingContext) throws MappingException {
        try {
            return getMapper(source).map(source, mappingContext);
        } catch(Exception e) {
            return ErrorHelper.rethrow(e);
        }
    }

    @Override
    public final void mapTo(ResultSet source, T target, MappingContext<ResultSet> mappingContext) throws Exception {
        getMapper(source).mapTo(source, target, mappingContext);
    }

    protected abstract Mapper<ResultSet, T> getMapper(ResultSet source) throws SQLException;
}
