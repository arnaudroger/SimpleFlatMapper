package org.sfm.reflect;

import java.lang.reflect.Field;

import org.junit.Test;
import org.sfm.beans.Foo;
import org.sfm.reflect.FieldSetter;

public class FieldSetterTest {

	@Test
	public void testSet() throws Exception {
		Field fooField = Foo.class.getDeclaredField("foo");
		fooField.setAccessible(true);
		
		FieldSetter<Foo, String> setter = new FieldSetter<Foo, String>(fooField);
		
		SetterTestHelper.validateFooSetter(setter);
	}



}
