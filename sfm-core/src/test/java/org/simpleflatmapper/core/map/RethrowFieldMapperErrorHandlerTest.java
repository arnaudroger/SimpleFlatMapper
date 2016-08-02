package  org.simpleflatmapper.core.map;

import org.junit.Test;
import  org.simpleflatmapper.core.map.error.RethrowFieldMapperErrorHandler;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class RethrowFieldMapperErrorHandlerTest {


	@Test
	public void test() {
		RethrowFieldMapperErrorHandler<String> handler = new RethrowFieldMapperErrorHandler<String>();
		Exception error = new Exception();
		try {
			handler.errorMappingField("prop", this, this, error);
			fail("Expected exception");
		} catch(Exception e) {
			assertSame(error, e);
		}
	}

}
