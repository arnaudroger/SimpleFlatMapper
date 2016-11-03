package org.simpleflatmapper.test.map;

import org.junit.Test;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;

import static org.junit.Assert.fail;

public class RethrowMapperBuilderErrorHandlerTest {

	@Test
	public void testGetterNotFound() {
		RethrowMapperBuilderErrorHandler handler = RethrowMapperBuilderErrorHandler.INSTANCE;
		try {
			handler.accessorNotFound("prop");
			fail("Expected exception");
		} catch(MapperBuildingException e) {
		}
	}

	@Test
	public void testSetterNotFound() {
		RethrowMapperBuilderErrorHandler handler = RethrowMapperBuilderErrorHandler.INSTANCE;
		try {
			handler.propertyNotFound(this.getClass(), "prop");
			fail("Expected exception");
		} catch(MapperBuildingException e) {
		}
	}

}
