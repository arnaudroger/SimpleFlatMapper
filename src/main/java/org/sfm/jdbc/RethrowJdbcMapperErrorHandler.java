package org.sfm.jdbc;

public class RethrowJdbcMapperErrorHandler implements JdbcMapperErrorHandler {

	@Override
	public void handlerError(Throwable t, Object target) {
		throw new HandlerErrorException("Handler has error " + t.getMessage() + " on " + target, t);
	}

}
