package org.atclements.setter.reflect;

import static org.junit.Assert.*;

import java.lang.reflect.InvocationTargetException;

import org.atclements.setter.beans.FooString;
import org.junit.Test;

public class MethodSetterTest {

	@Test
	public void testSet() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
		MethodSetter setter = new MethodSetter(FooString.class.getDeclaredMethod("setFoo", String.class));
		FooString foo = new FooString();
		assertNull(foo.getFoo());
		setter.set(foo, "FooValue");
		assertEquals("FooValue", foo.getFoo());
	}

}
