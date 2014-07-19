package org.flatmap.reflect;

import org.flatmap.beans.Foo;
import org.flatmap.reflect.MethodSetter;
import org.junit.Test;

public class MethodSetterTest {

	@Test
	public void testSet() throws Exception {
		MethodSetter<Foo, String> setter = new MethodSetter<Foo, String>(Foo.class.getDeclaredMethod("setFoo", String.class));
		SetterTestHelper.validateFooSetter(setter);
	}

}
