package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.DbFinalPrimitiveObject;
import org.sfm.reflect.asm.AsmInstantiatorDefinitionFactory;

import java.sql.ResultSet;
import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InstantiatorFactoryTest {

	public static final ReflectionService DISABLE_ASM = ReflectionService
			.disableAsm();
	public static final ReflectionService ASM = ReflectionService
			.newInstance();


	public static class MyClassWithFactoryMethod {

		private MyClassWithFactoryMethod(){
		}


		public static MyClassWithFactoryMethod valueOf(String val) {
			return new MyClassWithFactoryMethod();
		}
	}


	public static class MyClassWithFactoryMethodPrimitiveType {

		private MyClassWithFactoryMethodPrimitiveType(){
		}


		public static MyClassWithFactoryMethodPrimitiveType valueOf(long val) {
			return new MyClassWithFactoryMethodPrimitiveType();
		}
	}


	public static class MyClassWithEmptyFactoryMethod {

		private MyClassWithEmptyFactoryMethod(){
		}


		public static MyClassWithEmptyFactoryMethod valueOf() {
			return new MyClassWithEmptyFactoryMethod();
		}
	}



	public static class MyClassWithFactoryMethodAndConstructor {

		public MyClassWithFactoryMethodAndConstructor(String v1, String v2){
		}

		public static MyClassWithFactoryMethodAndConstructor valueOf(String val) {
			throw new UnsupportedOperationException();
		}
	}



	@Test
	public void testInstantiateConstructorWithArgsAllPr() throws Exception {

		Instantiator<ResultSet, DbFinalPrimitiveObject> instantiator =
				DISABLE_ASM.getInstantiatorFactory().getInstantiator(DbFinalPrimitiveObject.class, ResultSet.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalPrimitiveObject.class),
						new HashMap<Parameter, Getter<? super ResultSet, ?>>(), true);
		DbFinalPrimitiveObject object = instantiator.newInstance(null);
		assertNotNull(object);
	}

	@Test
	public void testInstantiateConstructorWithArgsAllPrAsm() throws Exception {
		Instantiator<ResultSet, DbFinalPrimitiveObject> instantiator =
				ASM.getInstantiatorFactory().getInstantiator(DbFinalPrimitiveObject.class,
						ResultSet.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalPrimitiveObject.class),
						new HashMap<Parameter, Getter<? super ResultSet, ?>>(), true);
		DbFinalPrimitiveObject object = instantiator.newInstance(null);
		assertNotNull(object);
	}


	@Test
	public void testInstantiateConstructorWithFactoryMethodWithPrimitiveAsm() throws Exception {
		Instantiator<Long, MyClassWithFactoryMethodPrimitiveType> instantiator =
				ASM.getInstantiatorFactory().getInstantiator(MyClassWithFactoryMethodPrimitiveType.class,
						long.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(MyClassWithFactoryMethodPrimitiveType.class),
						new HashMap<Parameter, Getter<? super Long, ?>>(), true);
		MyClassWithFactoryMethodPrimitiveType object = instantiator.newInstance(1l);
		assertNotNull(object);
	}
	@Test
	public void testInstantiateWithFactoryMethod() throws Exception {
		final Instantiator<ResultSet, MyClassWithFactoryMethod> instantiator = ASM.getInstantiatorFactory().getInstantiator(MyClassWithFactoryMethod.class, ResultSet.class, ASM.extractInstantiator(MyClassWithFactoryMethod.class), new HashMap<Parameter, Getter<? super ResultSet, ?>>(), true);

		assertTrue(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithFactoryMethod object = instantiator.newInstance(null);
		assertNotNull(object);

	}
	@Test
	public void testInstantiateWithFactoryMethodNoAsm() throws Exception {
		final Instantiator<ResultSet, MyClassWithFactoryMethod> instantiator = DISABLE_ASM.getInstantiatorFactory().getInstantiator(MyClassWithFactoryMethod.class, ResultSet.class, DISABLE_ASM.extractInstantiator(MyClassWithFactoryMethod.class), new HashMap<Parameter, Getter<? super ResultSet, ?>>(), true);

		assertFalse(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithFactoryMethod object = instantiator.newInstance(null);
		assertNotNull(object);
	}

	@Test
	public void testInstantiateWithEmptyFactoryMethodNoAsm() throws Exception {
		final Instantiator<ResultSet, MyClassWithEmptyFactoryMethod> instantiator = DISABLE_ASM.getInstantiatorFactory().getInstantiator(MyClassWithEmptyFactoryMethod.class, ResultSet.class, DISABLE_ASM.extractInstantiator(MyClassWithEmptyFactoryMethod.class), new HashMap<Parameter, Getter<? super ResultSet, ?>>(), true);

		assertFalse(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithEmptyFactoryMethod object = instantiator.newInstance(null);
		assertNotNull(object);
	}
	@Test
	public void testInstantiateCheckTakeConstructorFirst() throws Exception {

		final Instantiator<ResultSet, MyClassWithFactoryMethodAndConstructor> instantiator =
				ASM.getInstantiatorFactory().getInstantiator(MyClassWithFactoryMethodAndConstructor.class, ResultSet.class, ASM.extractInstantiator(MyClassWithFactoryMethodAndConstructor.class), new HashMap<Parameter, Getter<? super ResultSet, ?>>(), true);

		final MyClassWithFactoryMethodAndConstructor object = instantiator.newInstance(null);
		assertNotNull(object);
	}
	
}
