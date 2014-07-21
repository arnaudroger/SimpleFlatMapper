package org.sfm.reflect;

import java.lang.invoke.MethodHandles;

import org.junit.Test;
import org.sfm.beans.Foo;

public class MethodHandleSetterTest {

	@Test
	public void testSet() throws Exception {
		MethodHandleSetter<Foo, String> setter = new MethodHandleSetter<Foo, String>(MethodHandles.lookup().unreflect(Foo.class.getDeclaredMethod("setFoo", String.class)));
		SetterTestHelper.validateFooSetter(setter);
	}

}
