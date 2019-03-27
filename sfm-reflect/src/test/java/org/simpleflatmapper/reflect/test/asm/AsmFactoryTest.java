package org.simpleflatmapper.reflect.test.asm;

import org.junit.Test;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.ConstantIntGetter;
import org.simpleflatmapper.reflect.getter.ConstantLongGetter;
import org.simpleflatmapper.reflect.getter.OrdinalEnumGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbObject.Type;
import org.simpleflatmapper.util.UnaryFactory;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class AsmFactoryTest {

	static AsmFactory asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());
	
	
	@Test
	public void testSetterInteger() throws Exception {
		Getter<Pojo, Integer> getI = asmFactory.createGetter(Pojo.class.getDeclaredMethod("getI"));
		Setter<Pojo, Integer> setI = asmFactory.createSetter(Pojo.class.getDeclaredMethod("setI", Integer.class));
		
		Pojo p = new Pojo();
		
		assertNull(getI.get(p));
		
		setI.set(p, 123);
		assertEquals(new Integer(123), getI.get(p));
		
	}
	
	public class Pojo {
		Integer i;

		public Integer getI() {
			return i;
		}

		public void setI(Integer i) {
			this.i = i;
		}
	}
	
	@Test
	public void testCreateInstantiatorEmptyConstructor() throws Exception {
		Instantiator<Object, DbObject> instantiator = asmFactory.createEmptyArgsInstantiator(Object.class, DbObject.class);
		assertNotNull(instantiator.newInstance(null));
		assertSame(instantiator.getClass(), asmFactory.createEmptyArgsInstantiator(Object.class, DbObject.class).getClass());
	}
	@Test
	public void testCreateInstantiatorFinalDbObjectInjectIdAndName() throws Exception {
		ExecutableInstantiatorDefinition instantiatorDefinition =
				(ExecutableInstantiatorDefinition) AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalObject.class).get(0);
		HashMap<Parameter, Getter<? super Object, ?>> injections = new HashMap<Parameter, Getter<? super Object, ?>>();
		injections.put(new Parameter(0, "id", long.class), new ConstantLongGetter<Object>(33l));
		injections.put(new Parameter(1, "name", String.class), new ConstantGetter<Object, String>("fdo"));
		Instantiator<Object, DbFinalObject> instantiator = asmFactory.createInstantiator(Object.class,
				instantiatorDefinition,
				injections,
                true);
		

		DbFinalObject fdo = instantiator.newInstance(new Object());
		
		assertNotNull(fdo);
		assertNull(fdo.getEmail());
		assertNull(fdo.getCreationTime());
		assertNull(fdo.getTypeName());
		assertNull(fdo.getTypeOrdinal());
		assertEquals(33l, fdo.getId());
		assertEquals("fdo", fdo.getName());


		assertSame(instantiator.getClass(), asmFactory.createInstantiator(Object.class,
				instantiatorDefinition,
				injections,
                true).getClass());
	}
	
	@Test
	public void testCreateInstantiatorFinalDbObjectNameAndType() throws Exception {
		HashMap<Parameter, Getter<? super Object, ?>> injections = new HashMap<Parameter, Getter<? super Object, ?>>();
		ConstantIntGetter<Object> getter = new ConstantIntGetter<Object>(1);
		injections.put(new Parameter(4, "typeOrdinal", Type.class), new OrdinalEnumGetter<Object, Type>(getter, Type.class));
		injections.put(new Parameter(1, "name", String.class), new ConstantGetter<Object, String>("fdo"));

		List<InstantiatorDefinition> instantiatorDefinitions = AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalObject.class);
		Instantiator<Object, DbFinalObject> instantiator = asmFactory.createInstantiator(Object.class,
				(ExecutableInstantiatorDefinition) instantiatorDefinitions.get(0),
				injections,
                true);
		
		DbFinalObject fdo = instantiator.newInstance(new Object());
		
		assertNotNull(fdo);
		assertNull(fdo.getEmail());
		assertNull(fdo.getCreationTime());
		assertNull(fdo.getTypeName());
		assertEquals(0, fdo.getId());
		assertEquals("fdo", fdo.getName());
		assertEquals(Type.type2, fdo.getTypeOrdinal());
	}
	

	@Test
	public void testRegisterOrCreate() {
		AsmFactory asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());

		final MyService[] services = new MyService[] {new MyService(), new MyService() };
		UnaryFactory<AsmFactory, MyService> factory = new UnaryFactory<AsmFactory, MyService>() {
			int i = 0;

			@Override
			public MyService newInstance(AsmFactory asmFactory) {
				return services[i++];
			}
		};
		assertSame(services[0], asmFactory.registerOrCreate(MyService.class, factory));
		assertSame(services[0], asmFactory.registerOrCreate(MyService.class, factory));
	}

	public static class MyService {

	}
}
