package org.sfm.reflect;

import org.junit.Assert;
import org.junit.Test;
import org.sfm.beans.DbObject;

public class InstantiatorFactoryTest {

	@Test
	public void testInstantiateDbObject() throws Exception {
		Instantiator<DbObject> instantiator = new InstantiatorFactory().getInstantiator(DbObject.class);
		DbObject object = instantiator.newInstance();
		Assert.assertNotNull(object);
	}
	
}
