package org.atclements.setter.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.atclements.setter.Setter;
import org.atclements.setter.beans.FooString;

public class SetterTestHelper {
	public static void validateFooSetter(Setter setter)
			throws Exception {
		FooString foo = new FooString();
		assertNull(foo.getFoo());
		setter.set(foo, "FooValue");
		assertEquals("FooValue", foo.getFoo());
	}
	
	public static void validateBarSetter(Setter setter)
			throws Exception {
		FooString foo = new FooString();
		assertNull(foo.getBar());
		setter.set(foo, "BarValue");
		assertEquals("BarValue", foo.getBar());
	}
}
