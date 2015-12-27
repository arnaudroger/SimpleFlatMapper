package org.sfm.map;

import org.junit.Test;
import org.sfm.map.error.RethrowMapperBuilderErrorHandler;

import static org.junit.Assert.fail;

public class RethrowMapperBuilderErrorHandlerTest {

	@Test
	public void testGetterNotFound() {
		RethrowMapperBuilderErrorHandler handler = new RethrowMapperBuilderErrorHandler();
		try {
			handler.accessorNotFound("prop");
			fail("Expected exception");
		} catch(MapperBuildingException e) {
		}
	}

	@Test
	public void testSetterNotFound() {
		RethrowMapperBuilderErrorHandler handler = new RethrowMapperBuilderErrorHandler();
		try {
			handler.propertyNotFound(this.getClass(), "prop");
			fail("Expected exception");
		} catch(MapperBuildingException e) {
		}
	}

}
