package org.simpleflatmapper.map.error;

import org.simpleflatmapper.map.RowHandlerErrorHandler;
import org.simpleflatmapper.util.ErrorHelper;

public class RethrowRowHandlerErrorHandler implements RowHandlerErrorHandler {

	public static RethrowRowHandlerErrorHandler INSTANCE = new RethrowRowHandlerErrorHandler();

	private RethrowRowHandlerErrorHandler() {
	}

	@Override
	public void handlerError(Throwable t, Object target) {
        ErrorHelper.rethrow(t);
	}
}
