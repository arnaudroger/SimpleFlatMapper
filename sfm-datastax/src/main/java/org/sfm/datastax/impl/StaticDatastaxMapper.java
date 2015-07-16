package org.sfm.datastax.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.exceptions.DriverException;
import org.sfm.datastax.DatastaxMapper;
import org.sfm.map.*;
import org.sfm.utils.RowHandler;

import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END


public final class StaticDatastaxMapper<T> implements DatastaxMapper<T> {

	private final Mapper<Row, T> mapper;
	private final MappingContextFactory<? super Row> mappingContextFactory;
	private final RowHandlerErrorHandler errorHandler;

	public StaticDatastaxMapper(final Mapper<Row, T> mapper,
								final RowHandlerErrorHandler errorHandler,
								final MappingContextFactory<Row> mappingContextFactory) {
		this.errorHandler = errorHandler;
		this.mapper = mapper;
		this.mappingContextFactory = mappingContextFactory;
	}

	public T map(Row rs) throws MappingException {
		return mapper.map(rs);
	}

	@Override
	public T map(Row rs, MappingContext<? super Row> context) throws MappingException {
		return mapper.map(rs, context);
	}

	@Override
	public void mapTo(Row rs, T target, MappingContext<? super Row> context) throws Exception {
		mapper.mapTo(rs, target, context);
	}

	public <H extends RowHandler<? super T>> H forEach(final ResultSet rs, final H handler)
			throws MappingException {
		MappingContext<? super Row> context = newMappingContext();
		Mapper<Row, T> lMapper = this.mapper;
		Row row;
		while((row = rs.one()) != null) {
			T t = lMapper.map(row, context);
			try {
				handler.handle(t);
			} catch(Throwable error) {
				errorHandler.handlerError(error, t);
			}
		}
		return handler;
	}

	public Iterator<T> iterator(ResultSet rs) throws MappingException {
		return new ResultSetIterator<T>(rs, mapper, newMappingContext());
	}

	//IFJAVA8_START
	public Stream<T> stream(ResultSet rs) throws MappingException {
		ResultSetSpliterator<T> spliterator = new ResultSetSpliterator<T>(rs, mapper, newMappingContext());
		return StreamSupport.stream(spliterator, false);
	}
	//IFJAVA8_END

	private MappingContext<? super Row> newMappingContext() {
		return mappingContextFactory.newContext();
	}

	@Override
	public String toString() {
		return "StaticJdbcMapper{" + mapper + '}';
	}

	@Override
	public MappingContext<? super Row> newMappingContext(Row rs) throws DriverException {
		return newMappingContext();
	}
}
