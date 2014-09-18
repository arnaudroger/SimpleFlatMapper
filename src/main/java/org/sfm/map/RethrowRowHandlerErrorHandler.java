package org.sfm.map;

import org.sfm.jdbc.HandlerErrorException;

public class RethrowRowHandlerErrorHandler implements RowHandlerErrorHandler {

	@Override
	public void handlerError(Throwable t, Object target) {
		throw new HandlerErrorException("Handler has error " + t.getMessage() + " on " + target, t);
	}

}
