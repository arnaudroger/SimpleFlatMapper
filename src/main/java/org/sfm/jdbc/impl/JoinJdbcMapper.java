package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.*;
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



public final class JoinJdbcMapper<T> implements JdbcMapper<T> {

    private final JdbcMapper<T> mapper;
    private final RowHandlerErrorHandler errorHandler;
    private final MappingContextFactory<ResultSet> mappingContextFactory;

    public JoinJdbcMapper(JdbcMapper<T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<ResultSet> mappingContextFactory) {
        this.mapper = mapper;
        this.errorHandler = errorHandler;
        this.mappingContextFactory = mappingContextFactory;
    }

    @Override
    public T map(ResultSet source) throws MappingException {
        return map(source, null);
    }

    @Override
    public T map(ResultSet source, MappingContext<ResultSet> mappingContext) throws MappingException {
        return mapper.map(source, mappingContext);
    }

    @Override
    public void mapTo(ResultSet source, T target, MappingContext<ResultSet> mappingContext) throws Exception {
        mapper.mapTo(source, target, mappingContext);
    }

    @Override
	public <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
			throws SQLException, MappingException {
        try  {
            newForEachIterator(rs).forEach(handler);
            return handler;
        } catch(RuntimeException e) {
            throw e;
        } catch(SQLException e) {
            throw e;
        } catch(Exception e) {
            throw new MappingException(e.getMessage(), e);
        }
	}

    private JoinForEach<T> newForEachIterator(ResultSet rs) {
        return new JoinForEach<T>(mapper, newMappingContext(rs), errorHandler, rs);
    }

    public MappingContext<ResultSet> newMappingContext(ResultSet source) {
        return mappingContextFactory.newContext();
    }

    @Override
    @Deprecated
	public Iterator<T> iterate(ResultSet rs) throws SQLException,
			MappingException {
		return new ForEachIteratorIterator<T>(newForEachIterator(rs));
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
		return StreamSupport.stream(new ForEachIteratorSpliterator<T>(newForEachIterator(rs)), false);
	}

    //IFJAVA8_END


    private static class JoinForEach<T> implements ForEachIterator<T> {

        private final Mapper<ResultSet, T> mapper;
        private final MappingContext<ResultSet> mappingContext;
        private final RowHandlerErrorHandler rowHandlerErrorHandler;


        private final ResultSet resultSet;
        private T currentValue;

        private JoinForEach(Mapper<ResultSet, T> mapper, MappingContext<ResultSet> mappingContext, RowHandlerErrorHandler rowHandlerErrorHandler, ResultSet resultSet) {
            this.mapper = mapper;
            this.mappingContext = mappingContext;
            this.rowHandlerErrorHandler = rowHandlerErrorHandler;
            this.resultSet = resultSet;
        }


        @Override
        public boolean next(RowHandler<? super T> rowHandler) throws Exception {
            return forEach(true, rowHandler);
        }

        @Override
        public void forEach(RowHandler<? super T> rowHandler) throws Exception {
            forEach(false, rowHandler);
        }

        private boolean forEach(boolean stopOnNext, RowHandler<? super T> rowHandler) throws Exception {
            while (resultSet.next()) {

                mappingContext.handle(resultSet);

                if (mappingContext.broke(0)) {
                    if (currentValue != null) {
                        callHandler(rowHandler);
                        currentValue = mapper.map(resultSet, mappingContext);
                        if (stopOnNext) {
                            return true;
                        }
                    } else {
                        currentValue = mapper.map(resultSet, mappingContext);
                    }
                } else {
                    mapper.mapTo(resultSet, currentValue, mappingContext);
                }
            }

            if (currentValue != null) {
                callHandler(rowHandler);
                currentValue = null;
                return true;
            } else {
                return false;
            }
        }

        private void callHandler(RowHandler<? super T> rowHandler) throws Exception {
            try {
                rowHandler.handle(currentValue);
            } catch(Exception e) {
                rowHandlerErrorHandler.handlerError(e, currentValue);
            }

        }
    }

    @Override
    public String toString() {
        return "JoinJdbcMapper{" +
                "mapper=" + mapper +
                '}';
    }
}
