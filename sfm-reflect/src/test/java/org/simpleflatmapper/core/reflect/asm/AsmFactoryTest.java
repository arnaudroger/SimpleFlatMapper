package org.simpleflatmapper.core.reflect.asm;

import org.junit.Test;
import org.simpleflatmapper.core.reflect.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.Instantiator;
import org.simpleflatmapper.core.reflect.InstantiatorDefinition;
import org.simpleflatmapper.core.reflect.Parameter;
import org.simpleflatmapper.core.reflect.getter.ConstantGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantIntGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantLongGetter;
import org.simpleflatmapper.core.reflect.getter.OrdinalEnumGetter;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbObject.Type;

import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class AsmFactoryTest {

	static AsmFactory asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());
	
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
				injections
		);
		

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
				injections
		).getClass());
	}
	
	@Test
	public void testCreateInstantiatorFinalDbObjectNameAndType() throws Exception {
		HashMap<Parameter, Getter<? super Object, ?>> injections = new HashMap<Parameter, Getter<? super Object, ?>>();
		injections.put(new Parameter(4, "typeOrdinal", Type.class), new OrdinalEnumGetter<Object, Type>(new ConstantIntGetter<Object>(1), Type.class));
		injections.put(new Parameter(1, "name", String.class), new ConstantGetter<Object, String>("fdo"));

		List<InstantiatorDefinition> instantiatorDefinitions = AsmInstantiatorDefinitionFactory.extractDefinitions(DbFinalObject.class);
		Instantiator<Object, DbFinalObject> instantiator = asmFactory.createInstantiator(Object.class,
				(ExecutableInstantiatorDefinition) instantiatorDefinitions.get(0),
				injections
		);
		
		DbFinalObject fdo = instantiator.newInstance(new Object());
		
		assertNotNull(fdo);
		assertNull(fdo.getEmail());
		assertNull(fdo.getCreationTime());
		assertNull(fdo.getTypeName());
		assertEquals(0, fdo.getId());
		assertEquals("fdo", fdo.getName());
		assertEquals(Type.type2, fdo.getTypeOrdinal());
	}
	

}
