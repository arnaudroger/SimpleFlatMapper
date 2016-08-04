package  org.simpleflatmapper.core.map;

import org.junit.Test;
import  org.simpleflatmapper.core.map.error.LogMapperBuilderErrorHandler;

public class LogRethrowMapperBuilderErrorHandlerTest {

	@Test
	public void test() {
		LogMapperBuilderErrorHandler handler = new LogMapperBuilderErrorHandler();
		handler.accessorNotFound("hello");
		handler.propertyNotFound(LogMapperBuilderErrorHandler.class, "prop");
	}

}
