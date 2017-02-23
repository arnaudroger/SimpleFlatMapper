package org.simpleflatmapper.reflect.test;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.ReflectionInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.asm.AsmInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbObject.Type;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReflectionInstantiatorDefinitionFactoryTest {

	public static class ObjectWithFactoryMethod {
		public static ObjectWithFactoryMethod valueOf(String value) {
			return null;
		}

		public static Object valueOf(int value) {
			return null;
		}

		public static void doNothing(ObjectWithFactoryMethod value) {
		}

		private ObjectWithFactoryMethod(){
		}
	}

	@Test
	public void testExtractStaticFactoryMethod() throws NoSuchMethodException {
		List<InstantiatorDefinition> factoryMethod = ReflectionInstantiatorDefinitionFactory.extractDefinitions(ObjectWithFactoryMethod.class);
		assertEquals(1, factoryMethod.size());

		ExecutableInstantiatorDefinition id = (ExecutableInstantiatorDefinition) factoryMethod.get(0);

		assertEquals(ObjectWithFactoryMethod.class.getMethod("valueOf", String.class), id.getExecutable());
		assertEquals(1, id.getParameters().length);
		Assert.assertEquals(new Parameter(0, null, String.class), id.getParameters()[0]);
	}

	@Test
	public void testExtractStaticFactoryMethodAsm() throws NoSuchMethodException, IOException {
		List<InstantiatorDefinition> factoryMethod = AsmInstantiatorDefinitionFactory.extractDefinitions(ObjectWithFactoryMethod.class);
		assertEquals(1, factoryMethod.size());

		ExecutableInstantiatorDefinition id = (ExecutableInstantiatorDefinition) factoryMethod.get(0);

		assertEquals(ObjectWithFactoryMethod.class.getMethod("valueOf", String.class), id.getExecutable());
		assertEquals(1, id.getParameters().length);
		assertEquals(new Parameter(0, "value", String.class), id.getParameters()[0]);
	}

	@Test
	public void testExtractConstructorsDbObject() throws IOException, NoSuchMethodException, SecurityException {
		List<InstantiatorDefinition> dbObjectConstructors = ReflectionInstantiatorDefinitionFactory.extractDefinitions(DbObject.class);
		assertEquals(3, dbObjectConstructors.size());
		assertEquals(0, dbObjectConstructors.get(0).getParameters().length);
		assertEquals(DbObject.class.getConstructor(), ((ExecutableInstantiatorDefinition)dbObjectConstructors.get(0)).getExecutable());

	}

	@Test
	public void testExtractConstructorsFinalDbObject() throws IOException, NoSuchMethodException, SecurityException {

		List<InstantiatorDefinition> finalDbObjectConstructors = ReflectionInstantiatorDefinitionFactory.extractDefinitions(DbFinalObject.class);
		assertEquals(2, finalDbObjectConstructors.size());
		assertEquals(6, finalDbObjectConstructors.get(0).getParameters().length);

		assertEquals(long.class, finalDbObjectConstructors.get(0).getParameters()[0].getType());
		assertEquals(String.class, finalDbObjectConstructors.get(0).getParameters()[1].getType());
		assertEquals(String.class, finalDbObjectConstructors.get(0).getParameters()[2].getType());
		assertEquals(Date.class, finalDbObjectConstructors.get(0).getParameters()[3].getType());
		assertEquals(Type.class, finalDbObjectConstructors.get(0).getParameters()[4].getType());
		assertEquals(Type.class, finalDbObjectConstructors.get(0).getParameters()[5].getType());

		assertNull(finalDbObjectConstructors.get(0).getParameters()[0].getName());
		assertNull(finalDbObjectConstructors.get(0).getParameters()[1].getName());
		assertNull(finalDbObjectConstructors.get(0).getParameters()[2].getName());
		assertNull(finalDbObjectConstructors.get(0).getParameters()[3].getName());
		assertNull(finalDbObjectConstructors.get(0).getParameters()[4].getName());
		assertNull(finalDbObjectConstructors.get(0).getParameters()[5].getName());


		assertEquals(DbFinalObject.class.getConstructor(long.class, String.class, String.class, Date.class, Type.class, Type.class),
				((ExecutableInstantiatorDefinition)finalDbObjectConstructors.get(0)).getExecutable());

	}


	@Test
	public void testExtractConstructorsTuple2() throws IOException, NoSuchMethodException, SecurityException {

		List<InstantiatorDefinition> finalDbObjectConstructors = ReflectionInstantiatorDefinitionFactory.extractDefinitions(Tuples.typeDef(String.class, DbObject.class));
		assertEquals(1, finalDbObjectConstructors.size());
		assertEquals(2, finalDbObjectConstructors.get(0).getParameters().length);

		assertEquals(Object.class, finalDbObjectConstructors.get(0).getParameters()[0].getType());
		assertEquals(Object.class, finalDbObjectConstructors.get(0).getParameters()[1].getType());

		assertEquals(String.class, finalDbObjectConstructors.get(0).getParameters()[0].getGenericType());
		assertEquals(DbObject.class, finalDbObjectConstructors.get(0).getParameters()[1].getGenericType());

		assertNull(finalDbObjectConstructors.get(0).getParameters()[0].getName());
		assertNull(finalDbObjectConstructors.get(0).getParameters()[1].getName());

		assertEquals(Tuple2.class.getConstructor(Object.class, Object.class),
				((ExecutableInstantiatorDefinition)finalDbObjectConstructors.get(0)).getExecutable());

	}

	public static final ClassLoader CLASS_LOADER = new ClassLoader(ClassLoader.getSystemClassLoader().getParent()) {
		ClassLoader original = ReflectionInstantiatorDefinitionFactoryTest.class.getClassLoader();
		@Override
		protected Class<?> findClass(String name) throws ClassNotFoundException {
			String resourceName = name.replace(".", "/") + ".class";
			InputStream resourceAsStream = original.getResourceAsStream(resourceName);
			if (resourceAsStream == null) {
				System.out.println("Could not find resource " + resourceName + " in " + original);
				throw new ClassNotFoundException(name);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				int i;
				while((i = resourceAsStream.read()) != -1) {
					bos.write(i);
				}
				byte[] bytes = bos.toByteArray();
				return defineClass(name, bytes, 0, bytes.length);
			} catch (IOException e) {
				throw new ClassNotFoundException(e.getMessage(), e);
			} finally {
				try {
					resourceAsStream.close();
				} catch (IOException e) {
				}
			}

		}
	};

	//IFJAVA8_START
	@Test
	public void testClassWithParamName() throws ClassNotFoundException, IOException {
		final Class<?> classWithParameter = CLASS_LOADER.loadClass("p.ClassParameter");
		final Class<?> classWithoutParameter = CLASS_LOADER.loadClass("p.ClassNoNameParameter");
		final Class<?> classWithoutDebug = CLASS_LOADER.loadClass("p.ClassNoDebug");

		List<InstantiatorDefinition> instantiatorDefinitions = ReflectionService.newInstance().extractInstantiator(classWithParameter);

		assertEquals(2, instantiatorDefinitions.size());
		assertEquals(2, instantiatorDefinitions.get(0).getParameters().length);
		assertEquals("name", instantiatorDefinitions.get(0).getParameters()[0].getName());
		assertEquals("value", instantiatorDefinitions.get(0).getParameters()[1].getName());

		assertEquals(1, instantiatorDefinitions.get(1).getParameters().length);
		assertEquals("name", instantiatorDefinitions.get(1).getParameters()[0].getName());


		instantiatorDefinitions = ReflectionService.newInstance().extractInstantiator(classWithoutParameter);

		assertEquals(2, instantiatorDefinitions.size());
		assertEquals(2, instantiatorDefinitions.get(0).getParameters().length);
		assertNull(instantiatorDefinitions.get(0).getParameters()[0].getName());
		assertNull(instantiatorDefinitions.get(0).getParameters()[1].getName());

		assertEquals(1, instantiatorDefinitions.get(1).getParameters().length);
		assertNull(instantiatorDefinitions.get(1).getParameters()[0].getName());
	}
	//IFJAVA8_END



	@Test
	public void testInstantiatorWithExtraInstatiator() throws Exception {
		List<InstantiatorDefinition> instantiatorDefinitionsFactory =
				ReflectionService.newInstance().extractInstantiator(F.class, FF.class.getMethod("newInstance"));

		assertEquals(2, instantiatorDefinitionsFactory.size());

		assertEquals(InstantiatorDefinition.Type.CONSTRUCTOR, instantiatorDefinitionsFactory.get(0).getType());
		assertEquals(InstantiatorDefinition.Type.METHOD, instantiatorDefinitionsFactory.get(1).getType());

		List<InstantiatorDefinition> instantiatorDefinitionsBuilder =
				ReflectionService.newInstance().extractInstantiator(F.class, FB.class.getConstructor());

		assertEquals(2, instantiatorDefinitionsBuilder.size());
		assertEquals(InstantiatorDefinition.Type.CONSTRUCTOR, instantiatorDefinitionsBuilder.get(0).getType());
		assertEquals(InstantiatorDefinition.Type.BUILDER, instantiatorDefinitionsBuilder.get(1).getType());
	}


	public static class F {

	}

	public static class FF {
		public static F newInstance() {
			return new F();
		}
	}

	public static class FB {

		public FB withA(String a) {
			return this;
		}

		public F build() {
			return new F();
		}
	}






}
