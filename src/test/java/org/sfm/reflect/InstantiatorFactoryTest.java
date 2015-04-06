package org.sfm.reflect;

import org.junit.Assert;
import org.junit.Test;
import org.sfm.beans.DbFinalPrimitiveObject;
import org.sfm.reflect.asm.AsmInstantiatorDefinitionFactory;
import org.sfm.reflect.asm.AsmFactory;

import java.sql.ResultSet;
import java.util.HashMap;

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
	public void testInstantiateConstructorWithArgsAllPr() throws Exception {
		Instantiator<ResultSet, DbFinalPrimitiveObject> instantiator = new InstantiatorFactory(null).getInstantiator(DbFinalPrimitiveObject.class, ResultSet.class, AsmInstantiatorDefinitionFactory.<DbFinalPrimitiveObject>extractConstructors(DbFinalPrimitiveObject.class), new HashMap<Parameter, Getter<ResultSet, ?>>(), true);
		DbFinalPrimitiveObject object = instantiator.newInstance(null);
		Assert.assertNotNull(object);
	}

	@Test
	public void testInstantiateConstructorWithArgsAllPrAsm() throws Exception {
		Instantiator<ResultSet, DbFinalPrimitiveObject> instantiator = new InstantiatorFactory(new AsmFactory(Thread.currentThread().getContextClassLoader())).getInstantiator(DbFinalPrimitiveObject.class, ResultSet.class, AsmInstantiatorDefinitionFactory.<DbFinalPrimitiveObject>extractConstructors(DbFinalPrimitiveObject.class), new HashMap<Parameter, Getter<ResultSet, ?>>(), true);
		DbFinalPrimitiveObject object = instantiator.newInstance(null);
		Assert.assertNotNull(object);
	}

	
}
