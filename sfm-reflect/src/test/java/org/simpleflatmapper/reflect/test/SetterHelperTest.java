package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.setter.SetterHelper;
import org.simpleflatmapper.test.beans.Bar;
import org.simpleflatmapper.test.beans.BarField;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.test.beans.FooField;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;

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

	@Test
	public void testIsSetterOnSetMethodVoid() throws NoSuchMethodException {
		assertTrue(SetterHelper.isSetter(Setters.class.getMethod("setValue", String.class)));
	}

	@Test
	public void testIsSetterOnMethodVoidOneArg() throws NoSuchMethodException {
		assertTrue(SetterHelper.isSetter(Setters.class.getMethod("value", String.class)));
	}

	@Test
	public void testIsSetterOnMethodSetterReturnSameInstance() throws NoSuchMethodException {
		assertTrue(SetterHelper.isSetter(Setters.class.getMethod("valueBuilder", String.class)));
	}

	@Test
	public void testIsSetterOnMethodSetterReturnDiffType() throws NoSuchMethodException {
		assertFalse(SetterHelper.isSetter(Setters.class.getMethod("setValueReturn", String.class)));
	}

	@Test
	public void testIsSetterOnSetMethodArgs() throws NoSuchMethodException {
		assertFalse(SetterHelper.isSetter(Setters.class.getMethod("setValueArgs", String.class, String.class)));
	}


	@Test
	public void testSetterName() {
		assertEquals("getName", SetterHelper.getPropertyNameFromMethodName("getName"));
		assertEquals("name", SetterHelper.getPropertyNameFromMethodName("setName"));
		assertEquals("set", SetterHelper.getPropertyNameFromMethodName("set"));
		assertEquals("get", SetterHelper.getPropertyNameFromMethodName("get"));
		assertEquals("isName", SetterHelper.getPropertyNameFromMethodName("isName"));
		assertEquals("is", SetterHelper.getPropertyNameFromMethodName("is"));
	}

	@Test
	public void testBuilderSetterName() {
		assertEquals("getName", SetterHelper.getPropertyNameFromBuilderMethodName("getName"));
		assertEquals("name", SetterHelper.getPropertyNameFromBuilderMethodName("withName"));
		assertEquals("name", SetterHelper.getPropertyNameFromBuilderMethodName("setName"));
		assertEquals("set", SetterHelper.getPropertyNameFromBuilderMethodName("set"));
		assertEquals("get", SetterHelper.getPropertyNameFromBuilderMethodName("get"));
		assertEquals("isName", SetterHelper.getPropertyNameFromBuilderMethodName("isName"));
		assertEquals("is", SetterHelper.getPropertyNameFromBuilderMethodName("is"));
	}


	public static class Setters {
		public void setValue(String value) {
		}

		public void setValueArgs(String str, String str2) {
		}

		public void value(String value) {

		}

		public String setValueReturn(String value) {
			return null;
		}

		public Setters valueBuilder(String val) {
			return this;
		}
	}}
