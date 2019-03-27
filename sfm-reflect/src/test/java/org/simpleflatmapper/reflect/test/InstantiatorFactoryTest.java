package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.*;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.test.beans.DbFinalPrimitiveObject;
import org.simpleflatmapper.reflect.asm.AsmInstantiatorDefinitionFactory;

import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.simpleflatmapper.reflect.test.Utils.TEST_ASM_FACTORY_PROVIDER;

public class InstantiatorFactoryTest {

	public static final InstantiatorFactory DISABLE_ASM = new InstantiatorFactory(null);
	public static final InstantiatorFactory ASM = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);



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

		Instantiator<Object, DbFinalPrimitiveObject> instantiator =
				DISABLE_ASM.getInstantiator(DbFinalPrimitiveObject.class, Object.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalPrimitiveObject.class),
						new HashMap<Parameter, Getter<? super Object, ?>>(), true, true);
		DbFinalPrimitiveObject object = instantiator.newInstance(null);
		assertNotNull(object);
	}

	@Test
	public void testInstantiateConstructorWithArgsAllPrAsm() throws Exception {
		Instantiator<Object, DbFinalPrimitiveObject> instantiator =
				ASM.getInstantiator(DbFinalPrimitiveObject.class,
						Object.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalPrimitiveObject.class),
						new HashMap<Parameter, Getter<? super Object, ?>>(), true, true);
		DbFinalPrimitiveObject object = instantiator.newInstance(null);
		assertNotNull(object);
	}


	@Test
	public void testInstantiateConstructorWithFactoryMethodWithPrimitiveAsm() throws Exception {
		Instantiator<Long, MyClassWithFactoryMethodPrimitiveType> instantiator =
				ASM.getInstantiator(MyClassWithFactoryMethodPrimitiveType.class,
						long.class,
						AsmInstantiatorDefinitionFactory.extractDefinitions(MyClassWithFactoryMethodPrimitiveType.class),
						new HashMap<Parameter, Getter<? super Long, ?>>(), true, true);
		MyClassWithFactoryMethodPrimitiveType object = instantiator.newInstance(1l);
		assertNotNull(object);
	}
	@Test
	public void testInstantiateWithFactoryMethod() throws Exception {
		final Instantiator<Object, MyClassWithFactoryMethod> instantiator = ASM.getInstantiator(MyClassWithFactoryMethod.class, Object.class, ReflectionService.newInstance().extractInstantiator(MyClassWithFactoryMethod.class), new HashMap<Parameter, Getter<? super Object, ?>>(), true, true);

		assertTrue(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithFactoryMethod object = instantiator.newInstance(null);
		assertNotNull(object);

	}
	@Test
	public void testInstantiateWithFactoryMethodNoAsm() throws Exception {
		final Instantiator<Object, MyClassWithFactoryMethod> instantiator = DISABLE_ASM.getInstantiator(MyClassWithFactoryMethod.class, Object.class, ReflectionService.disableAsm().extractInstantiator(MyClassWithFactoryMethod.class), new HashMap<Parameter, Getter<? super Object, ?>>(), true, true);

		assertFalse(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithFactoryMethod object = instantiator.newInstance(null);
		assertNotNull(object);
	}

	@Test
	public void testInstantiateWithEmptyFactoryMethodNoAsm() throws Exception {
		final Instantiator<Object, MyClassWithEmptyFactoryMethod> instantiator = DISABLE_ASM.getInstantiator(MyClassWithEmptyFactoryMethod.class, Object.class, ReflectionService.disableAsm().extractInstantiator(MyClassWithEmptyFactoryMethod.class), new HashMap<Parameter, Getter<? super Object, ?>>(), true, true);

		assertFalse(instantiator.getClass().getSimpleName().startsWith("Asm"));
		final MyClassWithEmptyFactoryMethod object = instantiator.newInstance(null);
		assertNotNull(object);
	}
	@Test
	public void testInstantiateCheckTakeConstructorFirst() throws Exception {

		final Instantiator<Object, MyClassWithFactoryMethodAndConstructor> instantiator =
				ASM.getInstantiator(MyClassWithFactoryMethodAndConstructor.class, Object.class, ReflectionService.newInstance().extractInstantiator(MyClassWithFactoryMethodAndConstructor.class), new HashMap<Parameter, Getter<? super Object, ?>>(), true, true);

		final MyClassWithFactoryMethodAndConstructor object = instantiator.newInstance(null);
		assertNotNull(object);
	}

	@Test
	public void testOneArgInstantiator() throws  Exception {
		InstantiatorDefinition def = new ExecutableInstantiatorDefinition(String.class.getDeclaredMethod("valueOf", Object.class),
				new Parameter[] {new Parameter(0, "value", Object.class)});

		assertNotNull(def.toString());

		Instantiator<Object, String> instantiator = DISABLE_ASM.getOneArgIdentityInstantiator(def, true);

		assertEquals("12345", instantiator.newInstance(12345));

		Instantiator<Object, String> instantiatorAsm = ASM.getOneArgIdentityInstantiator(def, true);

		assertEquals("12345", instantiatorAsm.newInstance(12345));



	}

	@Test
	public void testArrayInstantiator() throws  Exception {
		assertArrayEquals(new int[3], DISABLE_ASM.<Object, int[]>getArrayInstantiator(int.class, 3).newInstance(null));
		assertArrayEquals(new int[3], ASM.<Object, int[]>getArrayInstantiator(int.class, 3).newInstance(null));
		assertArrayEquals(new String[3], DISABLE_ASM.<Object, String[]>getArrayInstantiator(String.class, 3).newInstance(null));
		assertArrayEquals(new String[3], ASM.<Object, String[]>getArrayInstantiator(String.class, 3).newInstance(null));
	}
	
}
