package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.*;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END


public final class JdbcMapperImpl<T> implements JdbcMapper<T> {


	private final Mapper<ResultSet, T> mapper;
	private final RowHandlerErrorHandler errorHandler;

	public JdbcMapperImpl(final  Mapper<ResultSet, T> mapper,
                          final RowHandlerErrorHandler errorHandler) {
		this.mapper = mapper;
		this.errorHandler = errorHandler;
	}

    @Override
    public final <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
            throws SQLException, MappingException {
        return JdbcMapperHelper.forEach(rs, handler, newMappingContext(rs), mapper, errorHandler);
    }

	@Override
    @Deprecated
	public Iterator<T> iterate(ResultSet rs) throws SQLException,
			MappingException {
		return new ResultSetIterator<T>(rs, mapper, newMappingContext(rs));
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
		return StreamSupport.stream(new ResultSetSpliterator<T>(rs, this, newMappingContext(rs)), false);
	}
	//IFJAVA8_END

	@Override
	public T map(ResultSet source) throws MappingException {
		return mapper.map(source);
	}

	@Override
	public T map(ResultSet source, MappingContext<ResultSet> context) throws MappingException {
		return mapper.map(source, context);
	}

	@Override
	public MappingContext<ResultSet> newMappingContext(ResultSet source) throws MappingException {
		return mapper.newMappingContext(source);
	}

	@Override
	public void mapTo(ResultSet source, T target, MappingContext<ResultSet> context) throws Exception {
		mapper.mapTo(source, target, context);
	}

	@Override
	public String toString() {
		return "JdbcMapperImpl{" + mapper + '}';
	}
}
