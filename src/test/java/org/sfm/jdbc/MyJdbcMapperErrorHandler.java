package org.sfm.jdbc;

public class MyJdbcMapperErrorHandler implements JdbcMapperErrorHandler {

	public Throwable error;

	@Override
	public void handlerError(Throwable t, Object target) {
		this.error = t;
	}

}
