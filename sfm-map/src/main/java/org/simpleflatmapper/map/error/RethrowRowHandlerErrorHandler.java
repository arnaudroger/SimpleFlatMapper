package org.simpleflatmapper.map.error;

import org.simpleflatmapper.map.RowHandlerErrorHandler;
import org.simpleflatmapper.util.ErrorHelper;

public class RethrowRowHandlerErrorHandler implements RowHandlerErrorHandler {

	@Override
	public void handlerError(Throwable t, Object target) {
        ErrorHelper.rethrow(t);
	}
}
