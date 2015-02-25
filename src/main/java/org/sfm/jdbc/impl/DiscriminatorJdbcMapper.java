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
import java.util.stream.Stream;

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
                for(int i = 0; i < mappingContexts.length; i++) {
                    mappingContexts[i].markAsBroken();
                }
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
		return null;
	}

	@Override
    @SuppressWarnings("deprecation")
    public final Iterator<T> iterator(final ResultSet rs)
			throws SQLException, MappingException {
		return null;
	}
	
	//IFJAVA8_START
	@Override
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return null;
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
