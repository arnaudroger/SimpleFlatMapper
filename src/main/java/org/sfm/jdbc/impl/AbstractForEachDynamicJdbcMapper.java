package org.sfm.jdbc.impl;

import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.ForEachIterator;
import org.sfm.utils.ForEachIteratorIterator;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import org.sfm.utils.ForEachIteratorSpliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public abstract class AbstractForEachDynamicJdbcMapper<T> extends AbstractDynamicJdbcMapper<T> {

    protected final RowHandlerErrorHandler errorHandler;

    public AbstractForEachDynamicJdbcMapper(RowHandlerErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }


    @Override
	public final <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
			throws SQLException, MappingException {
        try  {
            newForEachIterator(rs).forEach(handler);
            return handler;
        } catch(Exception e) {
            return JdbcMapperHelper.rethrowSQLException(e);
        }
	}

    protected abstract ForEachIterator<T> newForEachIterator(ResultSet rs) throws SQLException;

    @Override
    @Deprecated
	public final Iterator<T> iterate(ResultSet rs) throws SQLException,
			MappingException {
		return new ForEachIteratorIterator<T>(newForEachIterator(rs));
	}

	@Override
    @SuppressWarnings("deprecation")
    public final Iterator<T> iterator(ResultSet rs) throws SQLException,
			MappingException {
		return iterate(rs);
	}


    //IFJAVA8_START
	@Override
	public final Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return StreamSupport.stream(new ForEachIteratorSpliterator<T>(newForEachIterator(rs)), false);
	}

    //IFJAVA8_END

}
