package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.Foo;
import org.sfm.reflect.MethodSetter;

public class MethodSetterTest {

	@Test
	public void testSet() throws Exception {
		MethodSetter<Foo, String> setter = new MethodSetter<Foo, String>(Foo.class.getDeclaredMethod("setFoo", String.class));
		SetterTestHelper.validateFooSetter(setter);
	}

}
