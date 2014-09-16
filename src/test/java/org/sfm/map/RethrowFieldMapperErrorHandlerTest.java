package org.sfm.map;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RethrowFieldMapperErrorHandlerTest {


	@Test
	public void test() {
		RethrowFieldMapperErrorHandler<String> handler = new RethrowFieldMapperErrorHandler<String>();
		Exception error = new Exception();
		try {
			handler.errorMappingField("prop", this, this, error);
			fail("Expected exception");
		} catch(MappingException e) {
			assertSame(error, e.getCause());
		}
	}

}
