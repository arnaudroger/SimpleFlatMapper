package org.sfm.jdbc.impl;

import org.sfm.jdbc.*;
import org.sfm.map.*;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;
//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
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
    public MappingContext<ResultSet> newMappingContext(ResultSet source) throws MappingException {
        try {
            return getMapper(source).newMappingContext(source);
        } catch (SQLException e) {
            throw new SQLMappingException(e);
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
	public final <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
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


    private MappingContext<ResultSet>[] getMappingContexts(ResultSet rs) throws SQLException {
        @SuppressWarnings("unchecked")
        MappingContext<ResultSet>[] mappingContexts = new MappingContext[mappers.size()];

        int i = 0;
        for(Tuple2<Predicate<String>, Mapper<ResultSet, T>> tm : mappers) {
            mappingContexts[i] = tm.getElement1().newMappingContext(rs);
            i++;
        }
        return mappingContexts;
    }

    @Override
    @Deprecated
	public final Iterator<T> iterate(final ResultSet rs)
			throws SQLException, MappingException {
		return new ForEachIteratorIterator<T>(newForEachIterator(rs));
	}

    private DiscriminatorForEach<T> newForEachIterator(ResultSet rs) throws SQLException {
        return new DiscriminatorForEach<T>(this, getMappingContexts(rs), rowHandlerErrorHandler, rs);
    }

    @Override
    @SuppressWarnings("deprecation")
    public final Iterator<T> iterator(final ResultSet rs)
			throws SQLException, MappingException {
		return iterate(rs);
	}

    private Mapper<ResultSet,T> getMapper(int index) {
        return mappers.get(index).getElement1();
    }


    //IFJAVA8_START
    @Override
    public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
        return StreamSupport.stream(new ForEachIteratorSpliterator<T>(newForEachIterator(rs)), false);
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


    private static class DiscriminatorForEach<T> implements ForEachIterator<T> {

        private final DiscriminatorJdbcMapper<T> mapper;
        private final MappingContext<ResultSet>[] mappingContexts;
        private final RowHandlerErrorHandler rowHandlerErrorHandler;


        private final ResultSet resultSet;
        private int currentMapperIndex = -1;
        private T currentValue;
        private Mapper<ResultSet, T> currentMapper;
        private MappingContext<ResultSet> currentMappingContext;


        public DiscriminatorForEach(DiscriminatorJdbcMapper<T> mapper,
                                    MappingContext<ResultSet>[] mappingContexts,
                                    RowHandlerErrorHandler rowHandlerErrorHandler, ResultSet resultSet) {
            this.mapper = mapper;
            this.mappingContexts = mappingContexts;
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

                checkMapper();

                currentMappingContext.handle(resultSet);

                if (currentMappingContext.broke(0)) {
                    if (currentValue != null) {
                        callHandler(rowHandler);
                        currentValue = currentMapper.map(resultSet, currentMappingContext);
                        if (stopOnNext) {
                            return true;
                        }
                    } else {
                        currentValue = currentMapper.map(resultSet, currentMappingContext);
                    }
                } else {
                    currentMapper.mapTo(resultSet, currentValue, currentMappingContext);
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

        private void checkMapper() throws java.sql.SQLException {
            int mapperIndex = mapper.getMapperIndex(resultSet);
            if (currentMapperIndex != mapperIndex) {
                mapperChange(mapperIndex);
            }
        }

        private void mapperChange(int newMapperIndex) {
            markAsBroken(mappingContexts);
            currentMapper = this.mapper.getMapper(newMapperIndex);
            currentMappingContext = mappingContexts[newMapperIndex];
            currentMapperIndex = newMapperIndex;
        }

        private void markAsBroken(MappingContext<ResultSet>[] mappingContexts) {
            for(int i = 0; i < mappingContexts.length; i++) {
                mappingContexts[i].markAsBroken();
            }
        }
    }


}
