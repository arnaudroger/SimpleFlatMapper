package org.atclements.setter.reflect;

import static org.junit.Assert.*;

import org.atclements.setter.Setter;
import org.atclements.setter.beans.FooString;
import org.junit.Test;

public class ReflectionSetterFactoryTest {
	ReflectionSetterFactory factory = new ReflectionSetterFactory();
	@Test
	public void testDefaultToMethod() throws Exception {
		Setter setter = factory.getSetter(FooString.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	@Test
	public void testFallBackToField() throws Exception {
		Setter setter = factory.getSetter(FooString.class, "bar");
		assertTrue(setter instanceof FieldSetter);
		SetterTestHelper.validateBarSetter(setter);
	}
	
	@Test
	public void testReturnNullIfNotFound() throws Exception {
		Setter setter = factory.getSetter(FooString.class, "xxbar");
		assertNull(setter);
	}
}
