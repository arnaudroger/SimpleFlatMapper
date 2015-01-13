package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.asm.AsmFactory;

import java.sql.ResultSet;

import static org.junit.Assert.assertNotNull;

public class AsmInstantiatorTest {

	AsmFactory factory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	@Test
	public void testInstantiate() throws Exception {
		
		Instantiator<ResultSet, DbObject> instantiator = factory.createEmptyArgsInstatiantor(ResultSet.class, DbObject.class);
		
		assertNotNull(instantiator.newInstance(null));
	}

}
