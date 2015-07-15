package org.sfm.map.impl;

import org.sfm.map.RowHandlerErrorHandler;
import org.sfm.utils.ErrorHelper;

public class RethrowRowHandlerErrorHandler implements RowHandlerErrorHandler {

	@Override
	public void handlerError(Throwable t, Object target) {
        ErrorHelper.rethrow(t);
	}
}
