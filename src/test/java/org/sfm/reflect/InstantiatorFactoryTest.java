package org.sfm.reflect;

import java.sql.ResultSet;

import org.junit.Assert;
import org.junit.Test;
import org.sfm.beans.DbObject;

public class InstantiatorFactoryTest {

	static class MyClass {
		@SuppressWarnings("unused")
		private Object obj;
		@SuppressWarnings("unused")
		private int value;

		@SuppressWarnings("unused")
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
		Instantiator<ResultSet, DbObject> instantiator = new InstantiatorFactory(null).getInstantiator(ResultSet.class, DbObject.class);
		DbObject object = instantiator.newInstance(null);
		Assert.assertNotNull(object);
	}

	@Test
	public void testInstantiateConstructorWithArgs() throws Exception {
		Instantiator<ResultSet, MyClass> instantiator = new InstantiatorFactory(null).getInstantiator(ResultSet.class, MyClass.class);
		MyClass object = instantiator.newInstance(null);
		Assert.assertNotNull(object);
	}

}
