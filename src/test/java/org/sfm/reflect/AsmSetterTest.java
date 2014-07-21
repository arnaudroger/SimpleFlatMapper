package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.Foo;
import org.sfm.reflect.asm.AsmSetterFactory;

public class AsmSetterTest {

	@Test
	public void testSet() throws Exception {
		AsmSetterFactory factory = new AsmSetterFactory();
		Setter<Foo, String> setter = factory.createSetter(Foo.class.getDeclaredMethod("setFoo", String.class));
		SetterTestHelper.validateFooSetter(setter);
	}

}
