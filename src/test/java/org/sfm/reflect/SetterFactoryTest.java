package org.sfm.reflect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.Map;

import org.junit.Test;
import org.sfm.beans.Bar;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.Foo;
import org.sfm.reflect.asm.AsmSetterFactory;
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
import org.sfm.utils.PropertyNameMatcher;

public class SetterFactoryTest {
	SetterFactory nonAsmfactory = new SetterFactory(null);
	SetterFactory asmfactory = new SetterFactory();

	@Test
	public void testFailFallBackToMethod() throws Exception {
		Setter<Foo, String> setter = new SetterFactory(new AsmSetterFactory(){
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
		Setter<Bar, String> setter = nonAsmfactory.getSetter(Foo.class, "bar");
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
		assertTrue(nonAsmfactory.toBooleanSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pBoolean")) instanceof BooleanFieldSetter);
		assertTrue(nonAsmfactory.toBooleanSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean")) instanceof BooleanMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean");
		assertSame(setter, asmfactory.toBooleanSetter(setter));
	}
	
	@Test
	public void testToByteSetter() throws Exception {
		assertTrue(nonAsmfactory.toByteSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pByte")) instanceof ByteFieldSetter);
		assertTrue(nonAsmfactory.toByteSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte")) instanceof ByteMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte");
		assertSame(setter, asmfactory.toByteSetter(setter));
	}
	
	@Test
	public void testToCharacterSetter() throws Exception {
		assertTrue(nonAsmfactory.toCharacterSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pCharacter")) instanceof CharacterFieldSetter);
		assertTrue(nonAsmfactory.toCharacterSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter")) instanceof CharacterMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pCharacter");
		assertSame(setter, asmfactory.toCharacterSetter(setter));
	}
	
	@Test
	public void testToShortSetter() throws Exception {
		assertTrue(nonAsmfactory.toShortSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pShort")) instanceof ShortFieldSetter);
		assertTrue(nonAsmfactory.toShortSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort")) instanceof ShortMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort");
		assertSame(setter, asmfactory.toShortSetter(setter));
	}
	
	@Test
	public void testToIntSetter() throws Exception {
		assertTrue(nonAsmfactory.toIntSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pInt")) instanceof IntFieldSetter);
		assertTrue(nonAsmfactory.toIntSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt")) instanceof IntMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt");
		assertSame(setter, asmfactory.toIntSetter(setter));
	}
	
	@Test
	public void testToLongSetter() throws Exception {
		assertTrue(nonAsmfactory.toLongSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pLong")) instanceof LongFieldSetter);
		assertTrue(nonAsmfactory.toLongSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong")) instanceof LongMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong");
		assertSame(setter, asmfactory.toLongSetter(setter));
	}
	
	@Test
	public void testToFloatSetter() throws Exception {
		assertTrue(nonAsmfactory.toFloatSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pFloat")) instanceof FloatFieldSetter);
		assertTrue(nonAsmfactory.toFloatSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat")) instanceof FloatMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat");
		assertSame(setter, asmfactory.toFloatSetter(setter));
	}
	
	@Test
	public void testToDoubleSetter() throws Exception {
		assertTrue(nonAsmfactory.toDoubleSetter(nonAsmfactory.getSetter(DbPrimitiveObject.class, "pDouble")) instanceof DoubleFieldSetter);
		assertTrue(nonAsmfactory.toDoubleSetter(nonAsmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble")) instanceof DoubleMethodSetter);
		Setter<DbPrimitiveObjectWithSetter, Object> setter =  asmfactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble");
		assertSame(setter, asmfactory.toDoubleSetter(setter));
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testGetAllSetters() throws Exception {
		Map<String, Setter<Foo, Object>> setters = nonAsmfactory.getAllSetters(Foo.class);
		assertEquals(Foo.class.getDeclaredMethod("setFoo", String.class), ((MethodSetter)setters.get("foo")).getMethod());
		assertEquals(Bar.class.getDeclaredField("bar"), ((FieldSetter)setters.get("bar")).getField());
	}
	
	@SuppressWarnings("rawtypes")
	@Test
	public void testFindSetter() throws Exception {
		Setter<DbObject, Object> setter = nonAsmfactory.findSetter(new PropertyNameMatcher("id"), DbObject.class);
		assertEquals(DbObject.class.getDeclaredMethod("setId", long.class), ((MethodSetter)setter).getMethod());
	}
}
