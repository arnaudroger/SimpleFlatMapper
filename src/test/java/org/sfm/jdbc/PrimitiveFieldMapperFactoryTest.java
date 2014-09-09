package org.sfm.jdbc;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;

public class PrimitiveFieldMapperFactoryTest {
	
	SetterFactory setterFactory = new SetterFactory(null);
	PrimitiveFieldMapperFactory factory = new PrimitiveFieldMapperFactory(setterFactory);
	private FieldMapperErrorHandler errorHandler;

	@Test
	public void testPrimitiveFieldMapperWrontType() {
		Setter<DbObject, String> setter = setterFactory.getFieldSetter(DbObject.class, "name");
		
		try {
			factory.primitiveFieldMapper(1, setter, "id", errorHandler);
			fail("Expect error");
		} catch(UnsupportedOperationException e) {
			// expected
		}
		
		try {
			factory.primitiveFieldMapper("col1", setter, "id", errorHandler);
			fail("Expect error");
		} catch(UnsupportedOperationException e) {
			// expected
		}
	}


}
