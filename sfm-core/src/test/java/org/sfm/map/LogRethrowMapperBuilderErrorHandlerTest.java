package org.sfm.map;

import org.junit.Test;
import org.sfm.map.error.LogMapperBuilderErrorHandler;

public class LogRethrowMapperBuilderErrorHandlerTest {

	@Test
	public void test() {
		LogMapperBuilderErrorHandler handler = new LogMapperBuilderErrorHandler();
		handler.accessorNotFound("hello");
		handler.propertyNotFound(LogMapperBuilderErrorHandler.class, "prop");
	}

}
