package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.ObjectSetterFactory;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;
import org.simpleflatmapper.reflect.getter.FieldSetter;
import org.simpleflatmapper.reflect.setter.MethodSetter;
import org.simpleflatmapper.reflect.primitive.BooleanFieldSetter;
import org.simpleflatmapper.reflect.primitive.BooleanMethodSetter;
import org.simpleflatmapper.reflect.primitive.ByteFieldSetter;
import org.simpleflatmapper.reflect.primitive.ByteMethodSetter;
import org.simpleflatmapper.reflect.primitive.CharacterFieldSetter;
import org.simpleflatmapper.reflect.primitive.CharacterMethodSetter;
import org.simpleflatmapper.reflect.primitive.DoubleFieldSetter;
import org.simpleflatmapper.reflect.primitive.DoubleMethodSetter;
import org.simpleflatmapper.reflect.primitive.FloatFieldSetter;
import org.simpleflatmapper.reflect.primitive.FloatMethodSetter;
import org.simpleflatmapper.reflect.primitive.IntFieldSetter;
import org.simpleflatmapper.reflect.primitive.IntMethodSetter;
import org.simpleflatmapper.reflect.primitive.LongFieldSetter;
import org.simpleflatmapper.reflect.primitive.LongMethodSetter;
import org.simpleflatmapper.reflect.primitive.ShortFieldSetter;
import org.simpleflatmapper.reflect.primitive.ShortMethodSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObject;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.test.beans.FooField;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.simpleflatmapper.reflect.test.Utils.TEST_ASM_FACTORY_PROVIDER;

public class ObjectSetterFactoryTest {
	
	ObjectSetterFactory nonAsmFactory = new ObjectSetterFactory(null);
	ObjectSetterFactory asmFactory = new ObjectSetterFactory(TEST_ASM_FACTORY_PROVIDER);

	@Test
	public void testFailFallBackToMethod() throws Exception {
		final AsmFactory asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader()) {
			@Override
			public <T, P> Setter<T, P> createSetter(Method m) throws Exception {
				throw new UnsupportedOperationException();
			}
		};
		Setter<Foo, String> setter = new ObjectSetterFactory(new AsmFactoryProvider() {
			@Override
			public AsmFactory getAsmFactory(ClassLoader classLoader) {
				return asmFactory;
			}
		}).getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterHelperTest.validateFooSetter(setter);
	}
	
	@Test
	public void testMethodToAsm() throws Exception {
		Setter<Foo, String> setter = asmFactory.getSetter(Foo.class, "foo");
		assertFalse(setter instanceof MethodSetter);
		assertFalse(setter instanceof FieldSetter);
		SetterHelperTest.validateFooSetter(setter);
	}
	
	@Test
	public void testDefaultToMethod() throws Exception {
		Setter<Foo, String> setter = nonAsmFactory.getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterHelperTest.validateFooSetter(setter);
	}
	
	@Test
	public void testMatchFullMethodName() throws Exception {
		Setter<Foo, String> setter = nonAsmFactory.getSetter(Foo.class, "setFoo");
		assertFalse(setter instanceof FieldSetter);
		SetterHelperTest.validateFooSetter(setter);
	}
	
	@Test
	public void testFallBackToField() throws Exception {
		Setter<FallBackBar, String> setter = nonAsmFactory.getSetter(FallBackBar.class, "bar");
		assertTrue(setter instanceof FieldSetter);
		FallBackBar b = new FallBackBar();
		assertNull(b.getBar());
		setter.set(b, "bar");
		assertEquals("bar", b.getBar());
	}

	public class FallBackBar {
		private String bar;
		public String getBar() {
			return bar;
		}
	}


	@Test
	public void testReturnNullIfNotFound() throws Exception {
		Setter<Foo, String> setter = nonAsmFactory.getSetter(Foo.class, "xxbar");
		assertNull(setter);
	}


    @Test
    public void testPublicFieldAreAsm() throws Exception {
        Setter<FooField, String> setter = asmFactory.getSetter(FooField.class, "bar");
        assertFalse(setter instanceof FieldSetter);

        FooField ff = new FooField();

        setter.set(ff, "bar1");

        assertEquals("bar1", ff.bar);
    }
	
	@Test
	public void testToBooleanSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toBooleanSetter(nonAsmFactory.getSetter(DbPrimitiveObject.class, "pBoolean")) instanceof BooleanFieldSetter);
		assertTrue(ObjectSetterFactory.toBooleanSetter(nonAsmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean")) instanceof BooleanMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean");
		assertSame(setter, ObjectSetterFactory.toBooleanSetter(setter));
		assertNull(ObjectSetterFactory.toBooleanSetter(null));
		
		try {
			ObjectSetterFactory.toBooleanSetter(new Setter<DbPrimitiveObject, Boolean>() {
                @Override
                public void set(DbPrimitiveObject target, Boolean value) throws Exception {
                }
            });
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToByteSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toByteSetter(nonAsmFactory.getSetter(DbPrimitiveObject.class, "pByte")) instanceof ByteFieldSetter);
		assertTrue(ObjectSetterFactory.toByteSetter(nonAsmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte")) instanceof ByteMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte");
		assertSame(setter, ObjectSetterFactory.toByteSetter(setter));
		assertNull(ObjectSetterFactory.toByteSetter(null));

		try {
			ObjectSetterFactory.toByteSetter(new Setter<DbPrimitiveObject, Byte>() {
                @Override
                public void set(DbPrimitiveObject target, Byte value) throws Exception {
                }
            });
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToCharacterSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toCharacterSetter(nonAsmFactory.getSetter(DbPrimitiveObject.class, "pCharacter")) instanceof CharacterFieldSetter);
		assertTrue(ObjectSetterFactory.toCharacterSetter(nonAsmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter")) instanceof CharacterMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter");
		assertSame(setter, ObjectSetterFactory.toCharacterSetter(setter));
		assertNull(ObjectSetterFactory.toCharacterSetter(null));

		try {
			ObjectSetterFactory.toCharacterSetter(new Setter<DbPrimitiveObject, Character>() {
                @Override
                public void set(DbPrimitiveObject target, Character value) throws Exception {
                }
            });
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToShortSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toShortSetter(nonAsmFactory.getSetter(DbPrimitiveObject.class, "pShort")) instanceof ShortFieldSetter);
		assertTrue(ObjectSetterFactory.toShortSetter(nonAsmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort")) instanceof ShortMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort");
		assertSame(setter, ObjectSetterFactory.toShortSetter(setter));
		assertNull(ObjectSetterFactory.toShortSetter(null));

		try {
			ObjectSetterFactory.toShortSetter(new Setter<DbPrimitiveObject, Short>() {
                @Override
                public void set(DbPrimitiveObject target, Short value) throws Exception {
                }
            });
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToIntSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toIntSetter(nonAsmFactory.getSetter(DbPrimitiveObject.class, "pInt")) instanceof IntFieldSetter);
		assertTrue(ObjectSetterFactory.toIntSetter(nonAsmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt")) instanceof IntMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt");
		assertSame(setter, ObjectSetterFactory.toIntSetter(setter));
		assertNull(ObjectSetterFactory.toIntSetter(null));

		try {
			ObjectSetterFactory.toIntSetter(new Setter<DbPrimitiveObject, Integer>() {
                @Override
                public void set(DbPrimitiveObject target, Integer value) throws Exception {
                }
            });
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToLongSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toLongSetter(nonAsmFactory.getSetter(DbPrimitiveObject.class, "pLong")) instanceof LongFieldSetter);
		assertTrue(ObjectSetterFactory.toLongSetter(nonAsmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong")) instanceof LongMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong");
		assertSame(setter, ObjectSetterFactory.toLongSetter(setter));
		assertNull(ObjectSetterFactory.toLongSetter(null));

		try {
			ObjectSetterFactory.toLongSetter(new Setter<DbPrimitiveObject, Long>() {
                @Override
                public void set(DbPrimitiveObject target, Long value) throws Exception {
                }
            });
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToFloatSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toFloatSetter(nonAsmFactory.getSetter(DbPrimitiveObject.class, "pFloat")) instanceof FloatFieldSetter);
		assertTrue(ObjectSetterFactory.toFloatSetter(nonAsmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat")) instanceof FloatMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat");
		assertSame(setter, ObjectSetterFactory.toFloatSetter(setter));
		assertNull(ObjectSetterFactory.toFloatSetter(null));

		try {
			ObjectSetterFactory.toFloatSetter(new Setter<DbPrimitiveObject, Float>() {
                @Override
                public void set(DbPrimitiveObject target, Float value) throws Exception {
                }
            });
			fail("Should fail");
		} catch (Exception e) {
		}
	}
	
	@Test
	public void testToDoubleSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toDoubleSetter(nonAsmFactory.getSetter(DbPrimitiveObject.class, "pDouble")) instanceof DoubleFieldSetter);
		assertTrue(ObjectSetterFactory.toDoubleSetter(nonAsmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble")) instanceof DoubleMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble");
		assertSame(setter, ObjectSetterFactory.toDoubleSetter(setter));
		assertNull(ObjectSetterFactory.toDoubleSetter(null));

		try {
			ObjectSetterFactory.toDoubleSetter(new Setter<DbPrimitiveObject, Double>() {
                @Override
                public void set(DbPrimitiveObject target, Double value) throws Exception {
                }
            });
			fail("Should fail");
		} catch (Exception e) {
		}
	}
}
