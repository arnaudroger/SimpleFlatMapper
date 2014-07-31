package org.sfm.reflect;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.asm.AsmFactory;

public class AsmInstantiatorTest {

	AsmFactory factory = new AsmFactory();

	@Test
	public void testInstantiate() throws Exception {
		
		Instantiator<DbObject> instantiator = factory.createInstatiantor(DbObject.class);
		
		assertNotNull(instantiator.newInstance());
	}

}
