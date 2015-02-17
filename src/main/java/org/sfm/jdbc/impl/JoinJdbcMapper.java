package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.AbstractMapperImpl;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public final class JoinJdbcMapper<T> implements JdbcMapper<T> {

    private final BreakDetector<ResultSet> breakDetector;
    private final AbstractMapperImpl<ResultSet, T> mapper;
    private final RowHandlerErrorHandler errorHandler;

    public JoinJdbcMapper(BreakDetector breakDetector, AbstractMapperImpl<ResultSet, T> mapper, RowHandlerErrorHandler errorHandler) {
        this.breakDetector = breakDetector;
        this.mapper = mapper;
        this.errorHandler = errorHandler;
    }


    @Override
    public T map(ResultSet source) throws MappingException {
        return mapper.map(source);
    }

    @Override
	public <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
			throws SQLException, MappingException {

        T t = null;
		while(rs.next()) {
            if (t == null) {
                t = map(rs);
            } else  if (breakDetector.isBreaking(rs)) {
                if (t != null) {
                    callHandler(handler, t);
                }
                t = map(rs);
            } else {
                try {
                    mapper.mapFields(rs, t);
                } catch (Exception e) {
                    throw new MappingException(e.getMessage(), e);
                }
            }
		}

        if (t != null) {
            callHandler(handler, t);
        }
		return handler;
	}

    private <H extends RowHandler<? super T>> void callHandler(H handler, T t) {
        try {
            handler.handle(t);
        } catch(Throwable error) {
            errorHandler.handlerError(error, t);
        }
    }


    @Override
    @Deprecated
	public Iterator<T> iterate(ResultSet rs) throws SQLException,
			MappingException {
		return new ResultSetIterator<T>(rs, this);
	}

	@Override
    @SuppressWarnings("deprecation")
    public Iterator<T> iterator(ResultSet rs) throws SQLException,
			MappingException {
		return iterate(rs);
	}

	//IFJAVA8_START
	@Override
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return StreamSupport.stream(new AbstractJdbcMapperImpl.JdbcSpliterator<T>(rs, this), false);
	}
    //IFJAVA8_END
}
