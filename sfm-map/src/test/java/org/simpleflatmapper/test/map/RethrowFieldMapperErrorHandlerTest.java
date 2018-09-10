package org.simpleflatmapper.test.map;

import org.junit.Test;
import org.simpleflatmapper.map.error.RethrowFieldMapperErrorHandler;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class RethrowFieldMapperErrorHandlerTest {


	@Test
	public void test() {
		RethrowFieldMapperErrorHandler handler = RethrowFieldMapperErrorHandler.INSTANCE;
		Exception error = new Exception();
		try {
			handler.errorMappingField("prop", this, this, error, null);
			fail("Expected exception");
		} catch(Exception e) {
			assertSame(error, e);
		}
	}

}
