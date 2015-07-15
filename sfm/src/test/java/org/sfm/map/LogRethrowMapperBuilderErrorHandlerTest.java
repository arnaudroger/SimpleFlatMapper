package org.sfm.map;

import org.junit.Test;
import org.sfm.map.impl.LogRethrowMapperBuilderErrorHandler;

public class LogRethrowMapperBuilderErrorHandlerTest {

	@Test
	public void test() {
		LogRethrowMapperBuilderErrorHandler handler = new LogRethrowMapperBuilderErrorHandler();
		handler.getterNotFound("hello");
		handler.propertyNotFound(LogRethrowMapperBuilderErrorHandler.class, "prop");
	}

}
