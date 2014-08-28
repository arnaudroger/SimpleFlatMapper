package org.sfm.jdbc;

public interface JdbcMapperErrorHandler {
	void handlerError(Throwable t, Object target);
}
