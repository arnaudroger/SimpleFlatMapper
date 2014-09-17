package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.map.FieldMapper;
import org.sfm.map.MapperImpl;
import org.sfm.map.MappingException;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.RowHandler;

public final class JdbcMapperImpl<T> extends MapperImpl<ResultSet, T> implements JdbcMapper<T> {

	
	private final JdbcMapperErrorHandler errorHandler; 
	
	public JdbcMapperImpl(final FieldMapper<ResultSet, T>[] mappers, final Instantiator<ResultSet, T> instantiator, final JdbcMapperErrorHandler errorHandler) {
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
}
