package org.sfm.jdbc.impl;

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.FieldMapper;
import org.sfm.map.impl.MapperImpl;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END


public final class JdbcMapperImpl<T> extends MapperImpl<ResultSet, T> implements JdbcMapper<T> {

	
	private final RowHandlerErrorHandler errorHandler; 
	
	public JdbcMapperImpl(final FieldMapper<ResultSet, T>[] mappers, final Instantiator<ResultSet, T> instantiator, final RowHandlerErrorHandler errorHandler) {
		super(mappers, instantiator);
		this.errorHandler = errorHandler;
	}

	@Override
	public <H extends RowHandler<T>> H forEach(final ResultSet rs, final H handler)
			throws SQLException, MappingException {
		while(rs.next()) {
			T t = map(rs);
			try {
				handler.handle(t);
			} catch(Throwable error) {
				errorHandler.handlerError(error, t);
			}
		}
		return handler;
	}

	@Override
	public Iterator<T> iterate(ResultSet rs) throws SQLException,
			MappingException {
		return new ResultSetIterator<T>(rs, this);
	}

	//IFJAVA8_START
	@Override
	public Stream<T> stream(ResultSet rs) throws SQLException, MappingException {
		return StreamSupport.stream(new AbstractJdbcMapperImpl.JdbcSpliterator(rs, this), false);
	}
	//IFJAVA8_END
}
