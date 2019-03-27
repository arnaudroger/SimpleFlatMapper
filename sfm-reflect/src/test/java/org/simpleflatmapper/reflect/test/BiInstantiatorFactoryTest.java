package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;
import org.simpleflatmapper.reflect.asm.AsmInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.getter.BiFunctionGetter;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.test.beans.DbFinalPrimitiveObject;
import org.simpleflatmapper.util.BiFunction;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.simpleflatmapper.reflect.test.Utils.TEST_ASM_FACTORY_PROVIDER;

public class BiInstantiatorFactoryTest {

	public static final InstantiatorFactory DISABLE_ASM = new InstantiatorFactory(null);

	public static final InstantiatorFactory ASM = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);



	public static class MyClassWithEmptyConstructor {

		public MyClassWithEmptyConstructor(){
		}


	}

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
						new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>(), true, true);
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
						new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>(), true, true);
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
						new HashMap<Parameter, BiFunction<? super Long, ? super Long, ?>>(), true, true);
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
								new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>(), true, true);

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
								new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>(), true, true);

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
								new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>(), true, true);

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
								new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>(), true, true);

		final MyClassWithFactoryMethodAndConstructor object = instantiator.newInstance(null, null);
		assertNotNull(object);
	}


	@Test
	public void testInstantiateEmptyConstructorNoAsm() throws Exception {

		final BiInstantiator<Object, Object, MyClassWithEmptyConstructor> instantiator =
				DISABLE_ASM
						.getBiInstantiator(
								MyClassWithEmptyConstructor.class,
								Object.class,
								Object.class,
								ReflectionService.newInstance().extractInstantiator(MyClassWithEmptyConstructor.class),
								new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>(), true, true);

		final MyClassWithEmptyConstructor object = instantiator.newInstance(null, null);
		assertNotNull(object);
	}

	@Test
	public void testInstantiateEmptyConstructorAsm() throws Exception {

		final BiInstantiator<Object, Object, MyClassWithEmptyConstructor> instantiator =
				ASM
						.getBiInstantiator(
								MyClassWithEmptyConstructor.class,
								Object.class,
								Object.class,
								ReflectionService.newInstance().extractInstantiator(MyClassWithEmptyConstructor.class),
								new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>(), true, true);

		final MyClassWithEmptyConstructor object = instantiator.newInstance(null, null);
		assertNotNull(object);
	}

	@Test
	public void testBiInstantiatorOnDifferentGetter() throws Exception {
		Map<Parameter, BiFunction<? super Object, ? super Object, ?>> injections1 = new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>();
		Map<Parameter, BiFunction<? super Object, ? super Object, ?>> injections2 = new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>();


		injections1.put(new Parameter(0, "str", String.class), new BiFunctionGetter<Object, Object, String>(new ConstantGetter<Object, String>("str1")));
		injections1.put(new Parameter(1, "val1", int.class), new BiFunctionGetter<Object, Object, Integer>(new ConstantGetter<Object, Integer>(12)));

		injections2.put(new Parameter(0, "str", String.class), new BiFunctionGetter<Object, Object, String>(new ConstantGetter<Object, String>("str2")));
		injections2.put(new Parameter(1, "val1", int.class), new BiFunctionGetter<Object, Object, Integer>(new MyConstantGetter()));

		List<InstantiatorDefinition> constructors = ReflectionService.newInstance().extractInstantiator(ClassExample.class);


		final BiInstantiator<Object, Object, ClassExample> instantiator1 =
				ASM.getBiInstantiator(ClassExample.class, Object.class, Object.class,
						constructors,
						injections1,
						true, true);

		final BiInstantiator<Object, Object, ClassExample> instantiator2 =
				ASM.getBiInstantiator(ClassExample.class, Object.class, Object.class,
						constructors,
						injections2,
						true, true);


		ClassExample c1  = instantiator1.newInstance(null, null);
		assertEquals("str1", c1.getStr());
		assertEquals(12, c1.getVal1());
		ClassExample c2  = instantiator2.newInstance(null, null);
		assertEquals("str2", c2.getStr());
		assertEquals(13, c2.getVal1());
	}


	@Test
	public void testBiInstantiatorFailOnParameterMissMatch() throws Exception {
		Map<Parameter, BiFunction<? super Object, ? super Object, ?>> injections1 = new HashMap<Parameter, BiFunction<? super Object, ? super Object, ?>>();


		injections1.put(new Parameter(0, "str", String.class), new BiFunctionGetter<Object, Object, String>(new ConstantGetter<Object, String>("str1")));
		injections1.put(new Parameter(1, "val2", int.class), new BiFunctionGetter<Object, Object, Integer>(new ConstantGetter<Object, Integer>(12)));


		List<InstantiatorDefinition> constructors = ReflectionService.newInstance().extractInstantiator(ClassExample.class);

		try {

			final BiInstantiator<Object, Object, ClassExample> instantiator1 =
					ASM.getBiInstantiator(ClassExample.class, Object.class, Object.class,
							constructors,
							injections1,
							true, true);
			fail();
		} catch (IllegalArgumentException e) {
			// expected
		}

	}

	@Test
	public void testArrayBiInstantiator() throws  Exception {
		assertArrayEquals(new int[3], DISABLE_ASM.<Object, Object, int[]>getArrayBiInstantiator(int.class, 3).newInstance(null, null));
		assertArrayEquals(new int[3], ASM.<Object, Object, int[]>getArrayBiInstantiator(int.class, 3).newInstance(null, null));
		assertArrayEquals(new String[3], DISABLE_ASM.<Object, Object, String[]>getArrayBiInstantiator(String.class, 3).newInstance(null, null));
		assertArrayEquals(new String[3], ASM.<Object, Object,String[]>getArrayBiInstantiator(String.class, 3).newInstance(null, null));
	}

	public static class ClassExample {
		private final String str;
		private final int val1;

		public ClassExample(String str, int val1) {
			this.str = str;
			this.val1 = val1;
		}


		public String getStr() {
			return str;
		}

		public int getVal1() {
			return val1;
		}
	}

	private static class MyConstantGetter implements org.simpleflatmapper.reflect.Getter<Object,Integer> {
		@Override
		public Integer get(Object target) throws Exception {
			return 13;
		}
	}
}
