package org.atclements.setter.reflect;

import org.atclements.setter.beans.FooString;
import org.junit.Test;

public class MethodSetterTest {

	@Test
	public void testSet() throws Exception {
		MethodSetter setter = new MethodSetter(FooString.class.getDeclaredMethod("setFoo", String.class));
		SetterTestHelper.validateFooSetter(setter);
	}

}
