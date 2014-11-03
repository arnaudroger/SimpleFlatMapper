package org.sfm.map.impl;

import org.sfm.jdbc.HandlerErrorException;
import org.sfm.map.RowHandlerErrorHandler;

public class RethrowRowHandlerErrorHandler implements RowHandlerErrorHandler {

	@Override
	public void handlerError(Throwable t, Object target) {
		throw new HandlerErrorException("Handler has error " + t.getMessage() + " on " + target, t);
	}

}
