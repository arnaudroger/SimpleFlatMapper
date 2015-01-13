package org.sfm.reflect;

import org.sfm.beans.Bar;
import org.sfm.beans.Foo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SetterTestHelper {
	public static void validateFooSetter(Setter<Foo, String> setter)
			throws Exception {
		Foo foo = new Foo();
		assertNull(foo.getFoo());
		setter.set(foo, "FooValue");
		assertEquals("FooValue", foo.getFoo());
	}
	
	public static void validateBarSetter(Setter<Bar, String> setter)
			throws Exception {
		Foo foo = new Foo();
		assertNull(foo.getBar());
		setter.set(foo, "BarValue");
		assertEquals("BarValue", foo.getBar());
	}
}
