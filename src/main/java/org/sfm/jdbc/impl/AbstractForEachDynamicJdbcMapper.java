package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public abstract class AbstractForEachDynamicJdbcMapper<T> implements JdbcMapper<T> {

    protected final RowHandlerErrorHandler errorHandler;

    public AbstractForEachDynamicJdbcMapper(RowHandlerErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    @Override
    public T map(ResultSet rs) throws MappingException {
        return getMapper(rs).map(rs);
    }

    @Override
    public T map(ResultSet rs, MappingContext<ResultSet> context) throws MappingException {
        return getMapper(rs).map(rs, context);
    }

    @Override
    public void mapTo(ResultSet rs, T target, MappingContext<ResultSet> context) throws Exception {
        getMapper(rs).mapTo(rs, target, context);
    }

    @Override
	public final <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
			throws SQLException, MappingException {
        try  {
            newForEachIterator(rs).forEach(handler);
            return handler;
        } catch(Exception e) {
            return ErrorHelper.rethrow(e);
        }
	}

    protected abstract ForEachIterator<T> newForEachIterator(ResultSet rs) throws SQLException;

    @Override
	public final Iterator<T> iterator(ResultSet rs) throws SQLException,
			MappingException {
		return new ForEachIteratorIterator<T>(newForEachIterator(rs));
	}

    //IFJAVA8_START
	@Override
	public final Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return StreamSupport.stream(new ForEachIteratorSpliterator<T>(newForEachIterator(rs)), false);
	}
    //IFJAVA8_END

    protected abstract JdbcMapper<T> getMapper(final ResultSet rs);

    @Override
    public MappingContext<ResultSet> newMappingContext(ResultSet rs) throws SQLException {
        return getMapper(rs).newMappingContext(rs);
    }

}
