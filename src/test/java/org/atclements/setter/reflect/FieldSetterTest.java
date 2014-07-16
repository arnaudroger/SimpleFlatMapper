package org.atclements.setter.reflect;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.atclements.setter.beans.FooString;
import org.junit.Test;

public class FieldSetterTest {

	@Test
	public void testSet() throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field fooField = FooString.class.getDeclaredField("foo");
		fooField.setAccessible(true);
		
		FieldSetter setter = new FieldSetter(fooField);
		
		FooString foo = new FooString();
		assertNull(foo.getFoo());
		setter.set(foo, "FooValue");
		assertEquals("FooValue", foo.getFoo());
	}

}
