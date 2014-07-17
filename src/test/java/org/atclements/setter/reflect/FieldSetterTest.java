package org.atclements.setter.reflect;

import java.lang.reflect.Field;

import org.atclements.setter.beans.FooString;
import org.junit.Test;

public class FieldSetterTest {

	@Test
	public void testSet() throws Exception {
		Field fooField = FooString.class.getDeclaredField("foo");
		fooField.setAccessible(true);
		
		FieldSetter setter = new FieldSetter(fooField);
		
		SetterTestHelper.validateFooSetter(setter);
	}



}
