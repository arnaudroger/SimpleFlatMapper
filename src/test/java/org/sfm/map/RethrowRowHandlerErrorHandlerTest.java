package org.sfm.map;

import org.junit.Test;
import org.sfm.jdbc.HandlerErrorException;
import org.sfm.map.impl.RethrowRowHandlerErrorHandler;

import static org.junit.Assert.assertSame;

public class RethrowRowHandlerErrorHandlerTest {

	@Test
	public void testHandlerError() {
		RethrowRowHandlerErrorHandler handler = new RethrowRowHandlerErrorHandler();
		
		Exception error = new Exception();
		try {
			handler.handlerError(error, this);
		} catch(HandlerErrorException e) {
			assertSame(error, e.getCause());
		}
	}

}
