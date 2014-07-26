package org.sfm.reflect;

import org.junit.Assert;
import org.junit.Test;
import org.sfm.beans.DbObject;

public class InstantiatorFactoryTest {

	static class MyClass {
		@SuppressWarnings("unused")
		private Object obj;
		@SuppressWarnings("unused")
		private int value;

		private MyClass(Object obj) {
			throw new UnsupportedOperationException();
		}
		public MyClass(Object obj, int value) {
			this.obj = obj;
			this.value = value;
		}

		public MyClass(Object obj, int value, int v) {
			throw new UnsupportedOperationException();
		}

	}
	
	@Test
	public void testInstantiateDbObject() throws Exception {
		Instantiator<DbObject> instantiator = new InstantiatorFactory().getInstantiator(DbObject.class);
		DbObject object = instantiator.newInstance();
		Assert.assertNotNull(object);
	}

	@Test
	public void testInstantiateConstructorWithArgs() throws Exception {
		Instantiator<MyClass> instantiator = new InstantiatorFactory().getInstantiator(MyClass.class);
		MyClass object = instantiator.newInstance();
		Assert.assertNotNull(object);
	}

}
