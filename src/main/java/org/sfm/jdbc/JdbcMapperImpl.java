package org.sfm.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.sfm.map.FieldMapper;
import org.sfm.map.InstantiationMappingException;
import org.sfm.map.MappingException;
import org.sfm.reflect.Instantiator;
import org.sfm.utils.Handler;

public final class JdbcMapperImpl<T> implements JdbcMapper<T> {

	
	private final FieldMapper<ResultSet, T>[] fieldMappers;
	private final Instantiator<ResultSet, T> instantiator;
	private final JdbcMapperErrorHandler errorHandler; 
	
	public JdbcMapperImpl(final FieldMapper<ResultSet, T>[] mappers, final Instantiator<ResultSet, T> instantiator, final JdbcMapperErrorHandler errorHandler) {
		this.fieldMappers = mappers;
		this.instantiator = instantiator;
		this.errorHandler = errorHandler;
	}

	@Override
	public T map(final ResultSet source) throws MappingException {
		
		final T target;
		
		try {
			target = instantiator.newInstance(source);
		} catch(Exception e) {
			throw new InstantiationMappingException(e.getMessage(), e);
		}
		
		for(int i = 0; i < fieldMappers.length; i++) {
			fieldMappers[i].map(source, target);
		}
		return target;
	}
	
	@Override
	public <H extends Handler<T>> H forEach(final ResultSet rs, final H handler)
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
