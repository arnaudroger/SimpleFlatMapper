package org.simpleflatmapper.test.map;

import org.junit.Test;
import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;

import static org.junit.Assert.assertSame;

public class RethrowConsumerErrorHandlerTest {

	@Test
	public void testHandlerError() {
		RethrowConsumerErrorHandler handler = RethrowConsumerErrorHandler.INSTANCE;
		
		Exception error = new Exception();
		try {
			handler.handlerError(error, this);
		} catch(Exception e) {
			assertSame(error, e);
		}
	}

}
