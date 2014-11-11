package org.sfm.jdbc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;

//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END

import org.sfm.jdbc.JdbcMapper;
import org.sfm.map.MappingException;
import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.map.impl.AbstractMapperImpl;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public abstract class AbstractJdbcMapperImpl<T> extends AbstractMapperImpl<ResultSet, T> implements JdbcMapper<T> {
	
	private final RowHandlerErrorHandler errorHandler; 
	
	public AbstractJdbcMapperImpl(final Instantiator<ResultSet, T> instantiator, final RowHandlerErrorHandler errorHandler) {
		super(instantiator);
		this.errorHandler = errorHandler;
	}

	@Override
	public final <H extends RowHandler<T>> H forEach(final ResultSet rs, final H handler)
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
		Spliterator<T> spliterator = Spliterators.spliteratorUnknownSize(iterate(rs), Spliterator.DISTINCT | Spliterator.ORDERED);
		return StreamSupport.stream(spliterator, false);
	}
	//IFJAVA8_END

}
