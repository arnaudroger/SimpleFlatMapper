package org.flatmap.reflect;

import java.lang.reflect.Field;

import org.flatmap.beans.Foo;
import org.flatmap.reflect.FieldSetter;
import org.junit.Test;

public class FieldSetterTest {

	@Test
	public void testSet() throws Exception {
		Field fooField = Foo.class.getDeclaredField("foo");
		fooField.setAccessible(true);
		
		FieldSetter<Foo, String> setter = new FieldSetter<Foo, String>(fooField);
		
		SetterTestHelper.validateFooSetter(setter);
	}



}
