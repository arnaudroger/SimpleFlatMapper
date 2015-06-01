package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.Mapper;
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



public abstract class AbstractEnumarableJdbcMapper<T> implements JdbcMapper<T> {

    protected final RowHandlerErrorHandler errorHandler;

    public AbstractEnumarableJdbcMapper(RowHandlerErrorHandler errorHandler) {
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

            Enumarable<T> enumarable = newEnumarableOfT(rs);
            while(enumarable.next()) {
                handler.handle(enumarable.currentValue());
            }
            return handler;
        } catch(Exception e) {
            return ErrorHelper.rethrow(e);
        }
	}

    protected abstract Enumarable<T> newEnumarableOfT(ResultSet rs) throws SQLException;

    @Override
	public final Iterator<T> iterator(ResultSet rs) throws SQLException,
			MappingException {
		return new EnumarableIterator<T>(newEnumarableOfT(rs));
	}

    //IFJAVA8_START
	@Override
	public final Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return StreamSupport.stream(new EnumarableSpliterator<T>(newEnumarableOfT(rs)), false);
	}
    //IFJAVA8_END

    protected abstract Mapper<ResultSet, T> getMapper(final ResultSet rs);

}
