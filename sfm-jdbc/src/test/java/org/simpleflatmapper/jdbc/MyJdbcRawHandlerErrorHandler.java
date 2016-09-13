package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.map.ConsumerErrorHandler;

public class MyJdbcRawHandlerErrorHandler implements ConsumerErrorHandler {

	public Throwable error;

	@Override
	public void handlerError(Throwable t, Object target) {
		this.error = t;
	}

}
