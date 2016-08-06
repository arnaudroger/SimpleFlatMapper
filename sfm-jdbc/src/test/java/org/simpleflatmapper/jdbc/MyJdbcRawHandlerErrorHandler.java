package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.map.RowHandlerErrorHandler;

public class MyJdbcRawHandlerErrorHandler implements RowHandlerErrorHandler {

	public Throwable error;

	@Override
	public void handlerError(Throwable t, Object target) {
		this.error = t;
	}

}
