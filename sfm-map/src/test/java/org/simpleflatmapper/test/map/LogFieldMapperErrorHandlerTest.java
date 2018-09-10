package org.simpleflatmapper.test.map;

import org.junit.Test;
import org.simpleflatmapper.map.error.LogFieldMapperErrorHandler;

public class LogFieldMapperErrorHandlerTest {


	@Test
	public void testErrorMappingField() {
		LogFieldMapperErrorHandler<String> handler = new LogFieldMapperErrorHandler<String>();
		handler.errorMappingField("prop", this, this, new Exception(), null);
	}

}
