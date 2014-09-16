package org.sfm.map;

import org.junit.Test;

public class LogFieldMapperErrorHandlerTest {


	@Test
	public void testErrorMappingField() {
		LogFieldMapperErrorHandler<String> handler = new LogFieldMapperErrorHandler<String>();
		handler.errorMappingField("prop", this, this, new Exception());
	}

}
