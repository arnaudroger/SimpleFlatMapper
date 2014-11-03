package org.sfm.jdbc.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

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
}
