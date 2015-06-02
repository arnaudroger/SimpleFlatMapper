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


public final class StaticJdbcMapper<T> implements JdbcMapper<T> {

	private final Mapper<ResultSet, T> mapper;
	private final MappingContextFactory<ResultSet> mappingContextFactory;
	private final RowHandlerErrorHandler errorHandler;

	public StaticJdbcMapper(final Mapper<ResultSet, T> mapper,
							final RowHandlerErrorHandler errorHandler,
							final MappingContextFactory<ResultSet> mappingContextFactory) {
		this.errorHandler = errorHandler;
		this.mapper = mapper;
		this.mappingContextFactory = mappingContextFactory;
	}

	public T map(ResultSet rs) throws MappingException {
		return mapper.map(rs);
	}

	@Override
	public T map(ResultSet rs, MappingContext<ResultSet> context) throws MappingException {
		return mapper.map(rs, context);
	}

	@Override
	public void mapTo(ResultSet rs, T target, MappingContext<ResultSet> context) throws Exception {
		mapper.mapTo(rs, target, context);
	}

	public <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
			throws SQLException, MappingException {
		MappingContext<ResultSet> context = newMappingContext();
		Mapper<ResultSet, T> lMapper = this.mapper;
		while(rs.next()) {
			T t = lMapper.map(rs, context);
			try {
				handler.handle(t);
			} catch(Throwable error) {
				errorHandler.handlerError(error, t);
			}
		}
		return handler;
	}

	public Iterator<T> iterator(ResultSet rs) throws SQLException,
			MappingException {
		return new ResultSetIterator<T>(rs, mapper, newMappingContext());
	}

	//IFJAVA8_START
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		ResultSetSpliterator<T> spliterator = new ResultSetSpliterator<T>(rs, mapper, newMappingContext());
		return StreamSupport.stream(spliterator, false);
	}
	//IFJAVA8_END

	@Override
	public MappingContext<ResultSet> newMappingContext(ResultSet rs) {
		return newMappingContext();
	}

	private MappingContext<ResultSet> newMappingContext() {
		return mappingContextFactory.newContext();
	}

	@Override
	public String toString() {
		return "StaticJdbcMapper{" + mapper + '}';
	}
}
