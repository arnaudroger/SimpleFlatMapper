package org.sfm.reflect;

import org.sfm.beans.Bar;
import org.sfm.beans.BarField;
import org.sfm.beans.Foo;
import org.sfm.beans.FooField;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SetterHelperTest {
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

    public static void validateFooField(Setter<FooField, String> setter) throws Exception {
        FooField foo = new FooField();
        assertNull(foo.foo);
        setter.set(foo, "FooValue");
        assertEquals("FooValue", foo.foo);
    }

    public static void validateBarField(Setter<BarField, String> setter)
            throws Exception {
        FooField foo = new FooField();
        assertNull(foo.bar);
        setter.set(foo, "BarValue");
        assertEquals("BarValue", foo.bar);
    }
}
