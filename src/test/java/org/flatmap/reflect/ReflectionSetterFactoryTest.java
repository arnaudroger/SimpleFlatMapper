package org.flatmap.reflect;

import static org.junit.Assert.*;

import org.flatmap.beans.Bar;
import org.flatmap.beans.Foo;
import org.flatmap.reflect.FieldSetter;
import org.flatmap.reflect.MethodSetter;
import org.flatmap.reflect.ReflectionSetterFactory;
import org.flatmap.reflect.Setter;
import org.junit.Test;

public class ReflectionSetterFactoryTest {
	ReflectionSetterFactory factory = new ReflectionSetterFactory();
	@Test
	public void testDefaultToMethod() throws Exception {
		Setter<Foo, String> setter = factory.getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
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
