package org.sfm.map;

import static org.junit.Assert.*;

import org.junit.Test;

public class RethrowMapperBuilderErrorHandlerTest {

	@Test
	public void testGetterNotFound() {
		RethrowMapperBuilderErrorHandler handler = new RethrowMapperBuilderErrorHandler();
		try {
			handler.getterNotFound("prop");
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
