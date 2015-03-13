package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.*;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.FieldSetter;
import org.sfm.reflect.impl.MethodSetter;
import org.sfm.reflect.primitive.*;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ObjectSetterFactoryTest {
	
	ObjectSetterFactory nonAsmfactory = new ObjectSetterFactory(null);
	ObjectSetterFactory asmfactory = new ObjectSetterFactory(new AsmFactory(Thread.currentThread().getContextClassLoader()));

	@Test
	public void testFailFallBackToMethod() throws Exception {
		Setter<Foo, String> setter = new ObjectSetterFactory(new AsmFactory(Thread.currentThread().getContextClassLoader()){
			@Override
			public <T, P> Setter<T, P> createSetter(Method m) throws Exception {
				throw new UnsupportedOperationException();
			}
		}).getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterHelperTest.validateFooSetter(setter);
	}
	
	@Test
	public void testMethodToAsm() throws Exception {
		Setter<Foo, String> setter = asmfactory.getSetter(Foo.class, "foo");
		assertFalse(setter instanceof MethodSetter);
		assertFalse(setter instanceof FieldSetter);
		SetterHelperTest.validateFooSetter(setter);
	}
	
	@Test
	public void testDefaultToMethod() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterHelperTest.validateFooSetter(setter);
	}
	
	@Test
	public void testMatchFullMethodName() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "setFoo");
		assertFalse(setter instanceof FieldSetter);
		SetterHelperTest.validateFooSetter(setter);
	}
	
	@Test
	public void testFallBackToField() throws Exception {
		Setter<Bar, String> setter = nonAsmfactory.getSetter(Bar.class, "bar");
		assertTrue(setter instanceof FieldSetter);
		SetterHelperTest.validateBarSetter(setter);
	}
	
	@Test
	public void testReturnNullIfNotFound() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "xxbar");
		assertNull(setter);
	}


    @Test
    public void testPublicFieldAreAsm() throws Exception {
        Setter<FooField, String> setter = asmfactory.getSetter(FooField.class, "bar");
        assertFalse(setter instanceof FieldSetter);

        FooField ff = new FooField();

        setter.set(ff, "bar1");

        assertEquals("bar1", ff.bar);
    }
	
	@Test
	public void testToBooleanSetter() throws Exception {
		assertTrue(ObjectSetterFactory.toBooleanSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pBoolean")) instanceof BooleanFieldSetter);
		assertTrue(ObjectSetterFactory.toBooleanSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean")) instanceof BooleanMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean");
		assertSame(setter, ObjectSetterFactory.toBooleanSetter(setter));
		
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
		assertTrue(ObjectSetterFactory.toByteSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pByte")) instanceof ByteFieldSetter);
		assertTrue(ObjectSetterFactory.toByteSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte")) instanceof ByteMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte");
		assertSame(setter, ObjectSetterFactory.toByteSetter(setter));
		
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
		assertTrue(ObjectSetterFactory.toCharacterSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pCharacter")) instanceof CharacterFieldSetter);
		assertTrue(ObjectSetterFactory.toCharacterSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter")) instanceof CharacterMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter");
		assertSame(setter, ObjectSetterFactory.toCharacterSetter(setter));
		
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
		assertTrue(ObjectSetterFactory.toShortSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pShort")) instanceof ShortFieldSetter);
		assertTrue(ObjectSetterFactory.toShortSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort")) instanceof ShortMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort");
		assertSame(setter, ObjectSetterFactory.toShortSetter(setter));
		
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
		assertTrue(ObjectSetterFactory.toIntSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pInt")) instanceof IntFieldSetter);
		assertTrue(ObjectSetterFactory.toIntSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt")) instanceof IntMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt");
		assertSame(setter, ObjectSetterFactory.toIntSetter(setter));
		
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
		assertTrue(ObjectSetterFactory.toLongSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pLong")) instanceof LongFieldSetter);
		assertTrue(ObjectSetterFactory.toLongSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong")) instanceof LongMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong");
		assertSame(setter, ObjectSetterFactory.toLongSetter(setter));
		
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
		assertTrue(ObjectSetterFactory.toFloatSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pFloat")) instanceof FloatFieldSetter);
		assertTrue(ObjectSetterFactory.toFloatSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat")) instanceof FloatMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat");
		assertSame(setter, ObjectSetterFactory.toFloatSetter(setter));
		
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
		assertTrue(ObjectSetterFactory.toDoubleSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pDouble")) instanceof DoubleFieldSetter);
		assertTrue(ObjectSetterFactory.toDoubleSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble")) instanceof DoubleMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble");
		assertSame(setter, ObjectSetterFactory.toDoubleSetter(setter));
		
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
