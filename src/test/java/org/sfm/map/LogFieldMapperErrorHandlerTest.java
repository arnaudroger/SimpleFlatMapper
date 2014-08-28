package org.sfm.map;

import org.junit.Test;

public class LogFieldMapperErrorHandlerTest {


	@Test
	public void testErrorMappingField() {
		LogFieldMapperErrorHandler handler = new LogFieldMapperErrorHandler();
		handler.errorMappingField("prop", this, this, new Exception());
	}

}
