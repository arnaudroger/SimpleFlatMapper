package org.sfm.jdbc;

import static org.junit.Assert.*;

import org.junit.Test;

public class RethrowJdbcMapperErrorHandlerTest {

	@Test
	public void testHandlerError() {
		RethrowJdbcMapperErrorHandler handler = new RethrowJdbcMapperErrorHandler();
		
		Exception error = new Exception();
		try {
			handler.handlerError(error, this);
		} catch(HandlerErrorException e) {
			assertSame(error, e.getCause());
		}
	}

}
