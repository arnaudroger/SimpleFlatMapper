package org.sfm.reflect;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sfm.beans.Bar;
import org.sfm.beans.Foo;
import org.sfm.reflect.FieldSetter;
import org.sfm.reflect.MethodSetter;
import org.sfm.reflect.ReflectionSetterFactory;
import org.sfm.reflect.Setter;

public class ReflectionSetterFactoryTest {
	ReflectionSetterFactory factory = new ReflectionSetterFactory();
	@Test
	public void testDefaultToMethod() throws Exception {
		Setter<Foo, String> setter = factory.getSetter(Foo.class, "foo");
		assertFalse(setter instanceof FieldSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testMatchFullMethodName() throws Exception {
		Setter<Foo, String> setter = factory.getSetter(Foo.class, "setFoo");
		assertFalse(setter instanceof FieldSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	@Test
	public void testFallBackToField() throws Exception {
		Setter<Bar, String> setter = factory.getSetter(Foo.class, "bar");
		assertTrue(setter instanceof FieldSetter);
		SetterTestHelper.validateBarSetter(setter);
	}
	
	@Test
	public void testReturnNullIfNotFound() throws Exception {
		Setter<Foo, String> setter = factory.getSetter(Foo.class, "xxbar");
		assertNull(setter);
	}
}
