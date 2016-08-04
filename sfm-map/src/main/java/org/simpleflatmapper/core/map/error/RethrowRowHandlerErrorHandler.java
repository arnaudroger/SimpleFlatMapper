package org.simpleflatmapper.core.map.error;

import org.simpleflatmapper.core.map.RowHandlerErrorHandler;
import org.simpleflatmapper.util.ErrorHelper;

public class RethrowRowHandlerErrorHandler implements RowHandlerErrorHandler {

	@Override
	public void handlerError(Throwable t, Object target) {
        ErrorHelper.rethrow(t);
	}
}
