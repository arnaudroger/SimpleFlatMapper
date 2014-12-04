package org.sfm.reflect;

import java.sql.ResultSet;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import org.sfm.beans.DbFinalPrimitiveObject;
import org.sfm.beans.DbObject;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.asm.ConstructorDefinition;
import org.sfm.reflect.asm.ConstructorParameter;

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
	
	@Test
	public void testInstantiateConstructorWithArgsAllPr() throws Exception {
		Instantiator<ResultSet, DbFinalPrimitiveObject> instantiator = new InstantiatorFactory(null).getInstantiator(ResultSet.class,ConstructorDefinition.<DbFinalPrimitiveObject>extractConstructors(DbFinalPrimitiveObject.class), new HashMap<ConstructorParameter, Getter<ResultSet, ?>>());
		DbFinalPrimitiveObject object = instantiator.newInstance(null);
		Assert.assertNotNull(object);
	}

	@Test
	public void testInstantiateDbObjecAsm() throws Exception {
		Instantiator<ResultSet, DbObject> instantiator = new InstantiatorFactory(new AsmFactory()).getInstantiator(ResultSet.class, DbObject.class);
		DbObject object = instantiator.newInstance(null);
		Assert.assertNotNull(object);
	}

	@Test
	public void testInstantiateConstructorWithArgsAsm() throws Exception {
		Instantiator<ResultSet, MyClass> instantiator = new InstantiatorFactory(new AsmFactory()).getInstantiator(ResultSet.class, MyClass.class);
		MyClass object = instantiator.newInstance(null);
		Assert.assertNotNull(object);
	}
	
	@Test
	public void testInstantiateConstructorWithArgsAllPrAsm() throws Exception {
		Instantiator<ResultSet, DbFinalPrimitiveObject> instantiator = new InstantiatorFactory(new AsmFactory()).getInstantiator(ResultSet.class,ConstructorDefinition.<DbFinalPrimitiveObject>extractConstructors(DbFinalPrimitiveObject.class), new HashMap<ConstructorParameter, Getter<ResultSet, ?>>());
		DbFinalPrimitiveObject object = instantiator.newInstance(null);
		Assert.assertNotNull(object);
	}

	
}
