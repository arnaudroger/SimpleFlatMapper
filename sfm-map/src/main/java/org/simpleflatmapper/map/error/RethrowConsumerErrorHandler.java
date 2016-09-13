package org.simpleflatmapper.map.error;

import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.util.ErrorHelper;

public class RethrowConsumerErrorHandler implements ConsumerErrorHandler {

	public static RethrowConsumerErrorHandler INSTANCE = new RethrowConsumerErrorHandler();

	private RethrowConsumerErrorHandler() {
	}

	@Override
	public void handlerError(Throwable t, Object target) {
        ErrorHelper.rethrow(t);
	}
}
