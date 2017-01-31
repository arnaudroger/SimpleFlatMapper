package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmInstantiatorDefinitionFactory;
import org.simpleflatmapper.test.beans.DbFinalPrimitiveObject;
import org.simpleflatmapper.util.BiFactory;

import java.util.HashMap;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class BiInstantiatorFactoryTest {

	public static final InstantiatorFactory DISABLE_ASM = new InstantiatorFactory(null);
	public static final InstantiatorFactory ASM = new InstantiatorFactory(new AsmFactory(BiInstantiatorFactoryTest.class.getClassLoader()), true);


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

		BiInstantiator<Object, Object, DbFinalPrimitiveObject> instantiator =
				DISABLE_ASM.getBiInstantiator(DbFinalPrimitiveObject.class,
						Object.class,
						Object.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalPrimitiveObject.class),
						new HashMap<Parameter, BiFactory<? super Object, ? super Object, ?>>(), true);
		DbFinalPrimitiveObject object = instantiator.newInstance(null, null);
		assertNotNull(object);
	}

	@Test
	public void testInstantiateConstructorWithArgsAllPrAsm() throws Exception {
		BiInstantiator<Object, Object, DbFinalPrimitiveObject> instantiator =
				ASM.getBiInstantiator(DbFinalPrimitiveObject.class,
						Object.class,
						Object.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalPrimitiveObject.class),
						new HashMap<Parameter, BiFactory<? super Object, ? super Object, ?>>(), true);
		DbFinalPrimitiveObject object = instantiator.newInstance(null, null);
		assertNotNull(object);
	}


	@Test
	public void testInstantiateConstructorWithFactoryMethodWithPrimitiveAsm() throws Exception {
		BiInstantiator<Long, Long, MyClassWithFactoryMethodPrimitiveType> instantiator =
				ASM.getBiInstantiator(MyClassWithFactoryMethodPrimitiveType.class,
						long.class,
						long.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(MyClassWithFactoryMethodPrimitiveType.class),
						new HashMap<Parameter, BiFactory<? super Long, ? super Long, ?>>(), true);
		MyClassWithFactoryMethodPrimitiveType object = instantiator.newInstance(1l, 2l);
		assertNotNull(object);
	}
	@Test
	public void testInstantiateWithFactoryMethod() throws Exception {
		final BiInstantiator<Object, Object, MyClassWithFactoryMethod> instantiator =
				ASM
						.getBiInstantiator(MyClassWithFactoryMethod.class,
								Object.class,
								Object.class,
								ReflectionService.newInstance().extractInstantiator(MyClassWithFactoryMethod.class),
								new HashMap<Parameter, BiFactory<? super Object, ? super Object, ?>>(), true);

		assertTrue(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithFactoryMethod object = instantiator.newInstance(null, null);
		assertNotNull(object);

	}
	@Test
	public void testInstantiateWithFactoryMethodNoAsm() throws Exception {
		final BiInstantiator<Object, Object, MyClassWithFactoryMethod> instantiator =
				DISABLE_ASM
						.getBiInstantiator(
								MyClassWithFactoryMethod.class,
								Object.class,
								Object.class,
								ReflectionService.disableAsm().extractInstantiator(MyClassWithFactoryMethod.class),
								new HashMap<Parameter, BiFactory<? super Object, ? super Object, ?>>(), true);

		assertFalse(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithFactoryMethod object = instantiator.newInstance(null, null);
		assertNotNull(object);
	}

	@Test
	public void testInstantiateWithEmptyFactoryMethodNoAsm() throws Exception {
		final BiInstantiator<Object, Object, MyClassWithEmptyFactoryMethod> instantiator =
				DISABLE_ASM
						.getBiInstantiator(
								MyClassWithEmptyFactoryMethod.class,
								Object.class,
								Object.class,
								ReflectionService.disableAsm().extractInstantiator(MyClassWithEmptyFactoryMethod.class),
								new HashMap<Parameter, BiFactory<? super Object, ? super Object, ?>>(), true);

		assertFalse(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithEmptyFactoryMethod object = instantiator.newInstance(null, null);
		assertNotNull(object);
	}
	@Test
	public void testInstantiateCheckTakeConstructorFirst() throws Exception {

		final BiInstantiator<Object, Object, MyClassWithFactoryMethodAndConstructor> instantiator =
				ASM
						.getBiInstantiator(
								MyClassWithFactoryMethodAndConstructor.class,
								Object.class,
								Object.class,
								ReflectionService.newInstance().extractInstantiator(MyClassWithFactoryMethodAndConstructor.class),
								new HashMap<Parameter, BiFactory<? super Object, ? super Object, ?>>(), true);

		final MyClassWithFactoryMethodAndConstructor object = instantiator.newInstance(null, null);
		assertNotNull(object);
	}
}
