package org.sfm.reflect;

import static org.junit.Assert.*;

import java.lang.reflect.Method;

import org.junit.Test;
import org.sfm.beans.Bar;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.Foo;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.FieldSetter;
import org.sfm.reflect.impl.MethodSetter;
import org.sfm.reflect.primitive.BooleanFieldSetter;
import org.sfm.reflect.primitive.BooleanMethodSetter;
import org.sfm.reflect.primitive.ByteFieldSetter;
import org.sfm.reflect.primitive.ByteMethodSetter;
import org.sfm.reflect.primitive.CharacterFieldSetter;
import org.sfm.reflect.primitive.CharacterMethodSetter;
import org.sfm.reflect.primitive.DoubleFieldSetter;
import org.sfm.reflect.primitive.DoubleMethodSetter;
import org.sfm.reflect.primitive.FloatFieldSetter;
import org.sfm.reflect.primitive.FloatMethodSetter;
import org.sfm.reflect.primitive.IntFieldSetter;
import org.sfm.reflect.primitive.IntMethodSetter;
import org.sfm.reflect.primitive.LongFieldSetter;
import org.sfm.reflect.primitive.LongMethodSetter;
import org.sfm.reflect.primitive.ShortFieldSetter;
import org.sfm.reflect.primitive.ShortMethodSetter;

public class SetterFactoryTest {
	
	SetterFactory nonAsmfactory = new SetterFactory(null);
	SetterFactory asmfactory = new SetterFactory(new AsmFactory(Thread.currentThread().getContextClassLoader()));

	@Test
	public void testFailFallBackToMethod() throws Exception {
		Setter<Foo, String> setter = new SetterFactory(new AsmFactory(Thread.currentThread().getContextClassLoader()){
			@Override
			public <T, P> Setter<T, P> createSetter(Method m) throws Exception {
				throw new UnsupportedOperationException();
			}
		}).getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testMethodToAsm() throws Exception {
		Setter<Foo, String> setter = asmfactory.getSetter(Foo.class, "foo");
		assertFalse(setter instanceof MethodSetter);
		assertFalse(setter instanceof FieldSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testDefaultToMethod() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "foo");
		assertTrue(setter instanceof MethodSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testMatchFullMethodName() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "setFoo");
		assertFalse(setter instanceof FieldSetter);
		SetterTestHelper.validateFooSetter(setter);
	}
	
	@Test
	public void testFallBackToField() throws Exception {
		Setter<Bar, String> setter = nonAsmfactory.getSetter(Bar.class, "bar");
		assertTrue(setter instanceof FieldSetter);
		SetterTestHelper.validateBarSetter(setter);
	}
	
	@Test
	public void testReturnNullIfNotFound() throws Exception {
		Setter<Foo, String> setter = nonAsmfactory.getSetter(Foo.class, "xxbar");
		assertNull(setter);
	}
	
	@Test
	public void testToBooleanSetter() throws Exception {
		assertTrue(SetterFactory.toBooleanSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pBoolean")) instanceof BooleanFieldSetter);
		assertTrue(SetterFactory.toBooleanSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean")) instanceof BooleanMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean");
		assertSame(setter, SetterFactory.toBooleanSetter(setter));
		
		try {
			SetterFactory.toBooleanSetter(new Setter<DbPrimitiveObject, Boolean>() {
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
		assertTrue(SetterFactory.toByteSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pByte")) instanceof ByteFieldSetter);
		assertTrue(SetterFactory.toByteSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte")) instanceof ByteMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte");
		assertSame(setter, SetterFactory.toByteSetter(setter));
		
		try {
			SetterFactory.toByteSetter(new Setter<DbPrimitiveObject, Byte>() {
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
		assertTrue(SetterFactory.toCharacterSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pCharacter")) instanceof CharacterFieldSetter);
		assertTrue(SetterFactory.toCharacterSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter")) instanceof CharacterMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter");
		assertSame(setter, SetterFactory.toCharacterSetter(setter));
		
		try {
			SetterFactory.toCharacterSetter(new Setter<DbPrimitiveObject, Character>() {
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
		assertTrue(SetterFactory.toShortSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pShort")) instanceof ShortFieldSetter);
		assertTrue(SetterFactory.toShortSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort")) instanceof ShortMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort");
		assertSame(setter, SetterFactory.toShortSetter(setter));
		
		try {
			SetterFactory.toShortSetter(new Setter<DbPrimitiveObject, Short>() {
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
		assertTrue(SetterFactory.toIntSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pInt")) instanceof IntFieldSetter);
		assertTrue(SetterFactory.toIntSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt")) instanceof IntMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt");
		assertSame(setter, SetterFactory.toIntSetter(setter));
		
		try {
			SetterFactory.toIntSetter(new Setter<DbPrimitiveObject, Integer>() {
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
		assertTrue(SetterFactory.toLongSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pLong")) instanceof LongFieldSetter);
		assertTrue(SetterFactory.toLongSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong")) instanceof LongMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong");
		assertSame(setter, SetterFactory.toLongSetter(setter));
		
		try {
			SetterFactory.toLongSetter(new Setter<DbPrimitiveObject, Long>() {
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
		assertTrue(SetterFactory.toFloatSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pFloat")) instanceof FloatFieldSetter);
		assertTrue(SetterFactory.toFloatSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat")) instanceof FloatMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat");
		assertSame(setter, SetterFactory.toFloatSetter(setter));
		
		try {
			SetterFactory.toFloatSetter(new Setter<DbPrimitiveObject, Float>() {
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
		assertTrue(SetterFactory.toDoubleSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pDouble")) instanceof DoubleFieldSetter);
		assertTrue(SetterFactory.toDoubleSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble")) instanceof DoubleMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble");
		assertSame(setter, SetterFactory.toDoubleSetter(setter));
		
		try {
			SetterFactory.toDoubleSetter(new Setter<DbPrimitiveObject, Double>() {
				@Override
				public void set(DbPrimitiveObject target, Double value) throws Exception {
				}
			});
			fail("Should fail");
		} catch (Exception e) {
		}
	}
}
