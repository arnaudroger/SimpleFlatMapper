package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.*;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;
//IFJAVA8_START
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public final class JoinJdbcMapper<T> implements JdbcMapper<T> {

    private final Mapper<ResultSet, T> mapper;
    private final RowHandlerErrorHandler errorHandler;
    private final MappingContextFactory<ResultSet> mappingContextFactory;

    public JoinJdbcMapper(Mapper<ResultSet, T> mapper, RowHandlerErrorHandler errorHandler, MappingContextFactory<ResultSet> mappingContextFactory) {
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

        MappingContext<ResultSet> mappingContext = newMappingContext();
        T t = null;
		while(rs.next()) {
            mappingContext.handle(rs);
            if (mappingContext.broke(0)) {
                if (t != null) {
                    callHandler(handler, t);
                }
                t = map(rs, mappingContext);
            } else {
                try {
                    mapTo(rs, t, mappingContext);
                } catch(Exception e) {
                    throw new MappingException(e.getMessage(), e);
                }
            }
		}

        if (t != null) {
            callHandler(handler, t);
        }
		return handler;
	}

    private MappingContext<ResultSet> newMappingContext() {
        return mappingContextFactory.newContext();
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
		return new JoinOnResultSetIterator(rs);
	}

	@Override
    @SuppressWarnings("deprecation")
    public Iterator<T> iterator(ResultSet rs) throws SQLException,
			MappingException {
		return iterate(rs);
	}

    private class JoinOnResultSetIterator implements Iterator<T> {

        private final ResultSet rs;
        private final MappingContext<ResultSet> mappingContext;

        private boolean isFetched;
        private boolean hasValue;
        private T currentValue;
        private T nextValue;

        public JoinOnResultSetIterator(ResultSet rs) {
            this.rs = rs;
            this.mappingContext = newMappingContext();
        }

        @Override
        public boolean hasNext() {
            fetch();
            return hasValue;
        }

        private void fetch() {
            if (!isFetched) {
                try {
                    while (rs.next()) {
                        mappingContext.handle(rs);
                        if (mappingContext.broke(0)) {
                            if (currentValue == null) {
                                currentValue = JoinJdbcMapper.this.mapper.map(rs, mappingContext);
                            } else {
                                nextValue = JoinJdbcMapper.this.mapper.map(rs, mappingContext);
                                hasValue = true;
                                isFetched = true;
                                return;
                            }
                        } else {
                            try {
                                JoinJdbcMapper.this.mapper.mapTo(rs, currentValue, mappingContext);
                            } catch (Exception e) {
                                throw new MappingException(e.getMessage(), e);
                            }

                        }
                    }
                } catch (SQLException e) {
                    throw new MappingException(e.toString(), e);
                }

                if (currentValue != null) {
                    hasValue = true;
                    isFetched = true;
                } else {
                    hasValue = false;
                    isFetched = true;
                }
            }
        }

        @Override
        public T next() {
            fetch();
            if (hasValue) {
                T v = currentValue;
                currentValue = nextValue;
                nextValue = null;
                isFetched = false;
                return v;
            } else {
                throw new NoSuchElementException("No more rows");
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }


    //IFJAVA8_START
	@Override
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return StreamSupport.stream(new JoinOnJdbcSpliterator(rs), false);
	}

    private class JoinOnJdbcSpliterator implements Spliterator<T> {
        private final ResultSet resultSet;

        private T currentValue;
        private final MappingContext<ResultSet> mappingContext;


        public JoinOnJdbcSpliterator(ResultSet resultSet) {
            this.resultSet = resultSet;
            this.mappingContext = newMappingContext();
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            try {

                while (resultSet.next()) {
                    mappingContext.handle(resultSet);
                    if (mappingContext.broke(0)) {
                        if (currentValue != null) {
                            action.accept(currentValue);
                        }
                        currentValue = JoinJdbcMapper.this.mapper.map(resultSet, mappingContext);
                        return true;
                    } else {
                        try {
                            JoinJdbcMapper.this.mapper.mapTo(resultSet, currentValue, mappingContext);
                        } catch (Exception e) {
                            throw new MappingException(e.getMessage(), e);
                        }
                    }
                }

                if (currentValue != null) {
                    action.accept(currentValue);
                    currentValue = null;
                    return true;
                } else {
                    return false;
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void forEachRemaining(Consumer<? super T> action) {
            try {
                JoinJdbcMapper.this.forEach(resultSet, new RowHandler<T>() {
                    @Override
                    public void handle(T t) throws Exception {
                        action.accept(t);
                    }
                });
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Spliterator<T> trySplit() {
            return null;
        }

        @Override
        public long estimateSize() {
            return Long.MAX_VALUE;
        }

        @Override
        public int characteristics() {
            return Spliterator.ORDERED | Spliterator.NONNULL;
        }
    }
    //IFJAVA8_END
}
