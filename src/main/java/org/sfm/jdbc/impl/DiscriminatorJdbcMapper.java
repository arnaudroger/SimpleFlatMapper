package org.sfm.jdbc.impl;

import org.sfm.jdbc.*;
import org.sfm.map.*;
import org.sfm.map.impl.AbstractMapperImpl;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.Predicate;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

//IFJAVA8_START
//IFJAVA8_END


public final class DiscriminatorJdbcMapper<T> implements JdbcMapper<T> {


    private final String discriminatorColumn;
	private final RowHandlerErrorHandler rowHandlerErrorHandler;
    private final List<Tuple2<Predicate<String>, Mapper<ResultSet, T>>> mappers;

    public DiscriminatorJdbcMapper(String discriminatorColumn, List<Tuple2<Predicate<String>, Mapper<ResultSet, T>>> mappers, RowHandlerErrorHandler rowHandlerErrorHandler) {
        this.discriminatorColumn = discriminatorColumn;
        this.mappers = mappers;
        this.rowHandlerErrorHandler = rowHandlerErrorHandler;
    }


    @Override
    public final T map(ResultSet source) throws MappingException {
        return map(source, null);
    }

    @Override
	public final T map(final ResultSet source, final MappingContext<ResultSet> mappingContext) throws MappingException {
		try {
			final Mapper<ResultSet, ? extends T> mapper = getMapper(source);
			return mapper.map(source, mappingContext);
		} catch(SQLException e) {
			throw new SQLMappingException(e.getMessage(), e);
		}
	}

    @Override
    public final void mapTo(final ResultSet source, final T target, final MappingContext<ResultSet> mappingContext) throws MappingException {
        try {
            final Mapper<ResultSet, T> mapper = getMapper(source);
            mapper.mapTo(source, target, mappingContext);
        } catch(SQLException e) {
            throw new SQLMappingException(e.getMessage(), e);
        } catch(Exception e) {
            throw new MappingException(e.getMessage(), e);
        }
    }

	@Override
	public final <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handles)
			throws SQLException, MappingException {

        MappingContext<ResultSet>[] mappingContexts = getMappingContexts(rs);
        T t = null;
        int currentIndex = -1;
        while(rs.next()) {
            int index = getMapperIndex(rs);
            if (currentIndex != index) {
                markAsBroken(mappingContexts);
            }

            final MappingContext<ResultSet> mappingContext = mappingContexts[index];

            mappingContext.handle(rs);

            Mapper<ResultSet, T> mapper = mappers.get(index).getElement1();

            if (mappingContext.broke(0)) {
                if (t != null) {
                    callHandler(handles, t);
                }
                t = mapper.map(rs, mappingContext);
            } else {
                try {
                    mapper.mapTo(rs, t, mappingContext);
                } catch(Exception e) {
                    throw new MappingException(e.getMessage(), e);
                }
            }
            currentIndex = index;

        }

        if (t != null) {
            callHandler(handles, t);
        }

        return handles;
	}

    private void markAsBroken(MappingContext<ResultSet>[] mappingContexts) {
        for(int i = 0; i < mappingContexts.length; i++) {
            mappingContexts[i].markAsBroken();
        }
    }


    private <H extends RowHandler<? super T>> void callHandler(H handler, T t) {
        try {
            handler.handle(t);
        } catch(Throwable error) {
            rowHandlerErrorHandler.handlerError(error, t);
        }
    }


    private MappingContext<ResultSet>[] getMappingContexts(ResultSet rs) throws SQLException {
        MappingContext<ResultSet>[] mappingContexts = new MappingContext[mappers.size()];

        int i = 0;
        for(Tuple2<Predicate<String>, Mapper<ResultSet, T>> tm : mappers) {
            Mapper<ResultSet, T> mapper = tm.getElement1();
            if (mapper instanceof DynamicJdbcMapper) {
                mapper = ((DynamicJdbcMapper<T>)mapper).buildMapper(rs.getMetaData());
            }

            if (mapper instanceof JoinJdbcMapper) {
                mappingContexts[i] = ((JoinJdbcMapper) mapper).newMappingContext();
            } else {
                mappingContexts[i] = ((AbstractMapperImpl<ResultSet, T>) mapper).newMappingContext();
            }

            i++;
        }
        return mappingContexts;
    }

    @Override
    @Deprecated
	public final Iterator<T> iterate(final ResultSet rs)
			throws SQLException, MappingException {
		return new DiscriminatorResultSetIterator(rs);
	}

	@Override
    @SuppressWarnings("deprecation")
    public final Iterator<T> iterator(final ResultSet rs)
			throws SQLException, MappingException {
		return iterate(rs);
	}

    private class DiscriminatorResultSetIterator implements Iterator<T> {

        private final ResultSet rs;
        private final MappingContext<ResultSet>[] mappingContexts;

        private boolean isFetched;
        private boolean hasValue;
        private T currentValue;
        private T nextValue;
        private int currentIndex = -1;

        public DiscriminatorResultSetIterator(ResultSet rs) throws SQLException {
            this.rs = rs;
            this.mappingContexts = getMappingContexts(rs);
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

                        int index = getMapperIndex(rs);
                        if (currentIndex != index) {
                            markAsBroken(mappingContexts);
                        }

                        final MappingContext<ResultSet> mappingContext = mappingContexts[index];

                        mappingContext.handle(rs);

                        Mapper<ResultSet, T> mapper = mappers.get(index).getElement1();

                        if (mappingContext.broke(0)) {
                            if (currentValue == null) {
                                currentValue = mapper.map(rs, mappingContext);
                            } else {
                                nextValue = mapper.map(rs, mappingContext);
                                hasValue = true;
                                isFetched = true;
                                return;
                            }
                        } else {
                            try {
                               mapper.mapTo(rs, currentValue, mappingContext);
                            } catch (Exception e) {
                                throw new MappingException(e.getMessage(), e);
                            }

                        }
                        currentIndex = index;
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
        return StreamSupport.stream(new DiscriminatorJdbcSpliterator(rs), false);
    }

    private class DiscriminatorJdbcSpliterator implements Spliterator<T> {
        private final ResultSet resultSet;

        private T currentValue;
        private int currentIndex = -1;
        private final MappingContext<ResultSet>[] mappingContexts;


        public DiscriminatorJdbcSpliterator(ResultSet resultSet) throws SQLException {
            this.resultSet = resultSet;
            this.mappingContexts = getMappingContexts(resultSet);
        }

        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            try {

                while (resultSet.next()) {

                    int index = getMapperIndex(resultSet);
                    if (currentIndex != index) {
                        markAsBroken(mappingContexts);
                    }

                    final MappingContext<ResultSet> mappingContext = mappingContexts[index];

                    Mapper<ResultSet, T> mapper = mappers.get(index).getElement1();

                    mappingContext.handle(resultSet);
                    if (mappingContext.broke(0)) {
                        if (currentValue != null) {
                            action.accept(currentValue);
                        }
                        currentValue = mapper.map(resultSet, mappingContext);
                        currentIndex = index;
                        return true;
                    } else {
                        try {
                            mapper.mapTo(resultSet, currentValue, mappingContext);
                        } catch (Exception e) {
                            throw new MappingException(e.getMessage(), e);
                        }
                    }
                    currentIndex = index;
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
                DiscriminatorJdbcMapper.this.forEach(resultSet, new RowHandler<T>() {
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


    private Mapper<ResultSet, T> getMapper(final ResultSet rs) throws MappingException, SQLException {
        String value = rs.getString(discriminatorColumn);

        for(Tuple2<Predicate<String>, Mapper<ResultSet, T>> tm : mappers) {
            if (tm.first().test(value)) {
                return tm.second();
            }
        }
        throw new MappingException("No mapper found for " + discriminatorColumn + " = " + value);
    }
    private int getMapperIndex(final ResultSet rs) throws MappingException, SQLException {

        String value = rs.getString(discriminatorColumn);

        int i = 0;
        for(Tuple2<Predicate<String>, Mapper<ResultSet, T>> tm : mappers) {
            if (tm.first().test(value)) {
                return i;
            }
            i++;
        }
		throw new MappingException("No mapper found for " + discriminatorColumn + " = " + value);
	}

    @Override
    public String toString() {
        return "DiscriminatorJdbcMapper{" +
                "discriminatorColumn='" + discriminatorColumn + '\'' +
                ", mappers=" + mappers +
                '}';
    }



}
