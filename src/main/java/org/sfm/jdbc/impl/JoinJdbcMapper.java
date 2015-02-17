package org.sfm.jdbc.impl;

import org.sfm.jdbc.BreakDetectorFactory;
import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.Mapper;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.AbstractMapperImpl;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END



public final class JoinJdbcMapper<T> implements JdbcMapper<T> {

    private final BreakDetectorFactory<ResultSet> breakDetectorFactory;
    private final AbstractMapperImpl<ResultSet, T> mapper;
    private final RowHandlerErrorHandler errorHandler;

    public JoinJdbcMapper(BreakDetectorFactory breakDetectorFactory, AbstractMapperImpl<ResultSet, T> mapper, RowHandlerErrorHandler errorHandler) {
        this.breakDetectorFactory = breakDetectorFactory;
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

        BreakDetector<ResultSet> breakDetector = breakDetectorFactory.newInstance();
        T t = null;
		while(rs.next()) {
            if (breakDetector.isBreaking(rs)) {
                callHandler(handler, t);
                t = map(rs);
            } else {
                if (t == null) {
                    t = mapper.map(rs);
                } else {
                    try {
                        mapper.mapFields(rs, t);
                    } catch (Exception e) {
                        throw new MappingException(e.getMessage(), e);
                    }
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

        private boolean isFetched;
        private boolean hasValue;
        private final BreakDetector<ResultSet> resultSetBreakDetector;
        private T currentValue;
        private T nextValue;

        public JoinOnResultSetIterator(ResultSet rs) {
            this.rs = rs;
            this.resultSetBreakDetector = JoinJdbcMapper.this.breakDetectorFactory.newInstance();
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
                        if (resultSetBreakDetector.isBreaking(rs)) {
                            nextValue = JoinJdbcMapper.this.mapper.map(rs);
                            hasValue = true;
                            isFetched = true;
                            return;
                        } else {
                            if (currentValue == null) {
                                currentValue = JoinJdbcMapper.this.mapper.map(rs);
                            } else {
                                try {
                                    JoinJdbcMapper.this.mapper.mapFields(rs, currentValue);
                                } catch (Exception e) {
                                    throw new MappingException(e.getMessage(), e);
                                }
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
        private final BreakDetector<ResultSet> resultSetBreakDetector;

        private T currentValue;


        public JoinOnJdbcSpliterator(ResultSet resultSet) {
            this.resultSet = resultSet;
            this.resultSetBreakDetector = JoinJdbcMapper.this.breakDetectorFactory.newInstance();
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            try {

                while (resultSet.next()) {
                    if (resultSetBreakDetector.isBreaking(resultSet)) {
                        action.accept(currentValue);
                        currentValue = JoinJdbcMapper.this.mapper.map(resultSet);
                        return true;
                    } else {
                        if (currentValue == null) {
                            currentValue = JoinJdbcMapper.this.mapper.map(resultSet);
                        } else {
                            try {
                                JoinJdbcMapper.this.mapper.mapFields(resultSet, currentValue);
                            } catch (Exception e) {
                                throw new MappingException(e.getMessage(), e);
                            }
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
