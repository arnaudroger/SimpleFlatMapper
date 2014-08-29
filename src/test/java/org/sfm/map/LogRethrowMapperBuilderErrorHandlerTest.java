package org.sfm.map;

import org.junit.Test;

public class LogRethrowMapperBuilderErrorHandlerTest {

	@Test
	public void test() {
		LogRethrowMapperBuilderErrorHandler handler = new LogRethrowMapperBuilderErrorHandler();
		handler.getterNotFound("hello");
		handler.setterNotFound(LogRethrowMapperBuilderErrorHandler.class, "prop");
	}

}
