package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.Foo;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.primitive.*;

import static org.junit.Assert.assertEquals;

public class AsmSetterTest {

	AsmFactory factory = new AsmFactory(Thread.currentThread().getContextClassLoader());

	DbPrimitiveObjectWithSetter object = new DbPrimitiveObjectWithSetter();
	
	@Test
	public void testSet() throws Exception {
		Setter<Foo, String> setter = factory.createSetter(Foo.class.getDeclaredMethod("setFoo", String.class));
		SetterTestHelper.validateFooSetter(setter);
	}

	@Test
	public void testSetBoolean() throws Exception {
		@SuppressWarnings("unchecked")
		BooleanSetter<DbPrimitiveObjectWithSetter> setter = 
				(BooleanSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpBoolean", boolean.class));
		setter.setBoolean(object, true);
		assertEquals(true, object.ispBoolean());
	}
	
	@Test
	public void testSetByte() throws Exception {
		@SuppressWarnings("unchecked")
		ByteSetter<DbPrimitiveObjectWithSetter> setter = 
				(ByteSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpByte", byte.class));
		setter.setByte(object, (byte)0xc3);
		assertEquals((byte)0xc3, object.getpByte());
	}
	
	@Test
	public void testSetCharacter() throws Exception {
		@SuppressWarnings("unchecked")
		CharacterSetter<DbPrimitiveObjectWithSetter> setter = 
				(CharacterSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpCharacter", char.class));
		setter.setCharacter(object, 'g');
		assertEquals('g', object.getpCharacter());
	}
	
	@Test
	public void testSetShort() throws Exception {
		@SuppressWarnings("unchecked")
		ShortSetter<DbPrimitiveObjectWithSetter> setter = 
				(ShortSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpShort", short.class));
		setter.setShort(object, (short)33);
		assertEquals((short)33, object.getpShort());
	}
	
	@Test
	public void testSetInt() throws Exception {
		@SuppressWarnings("unchecked")
		IntSetter<DbPrimitiveObjectWithSetter> setter = 
				(IntSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpInt", int.class));
		setter.setInt(object, 35);
		assertEquals(35, object.getpInt());
	}
	
	@Test
	public void testSetLong() throws Exception {
		@SuppressWarnings("unchecked")
		LongSetter<DbPrimitiveObjectWithSetter> setter = 
				(LongSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpLong", long.class));
		setter.setLong(object, 35l);
		assertEquals(35l, object.getpLong());
	}
	
	@Test
	public void testSetFloat() throws Exception {
		@SuppressWarnings("unchecked")
		FloatSetter<DbPrimitiveObjectWithSetter> setter = 
				(FloatSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpFloat", float.class));
		setter.setFloat(object, 3.14f);
		assertEquals(3.14f, object.getpFloat(), 0);
	}

	@Test
	public void testSetDouble() throws Exception {
		@SuppressWarnings("unchecked")
		DoubleSetter<DbPrimitiveObjectWithSetter> setter = 
				(DoubleSetter<DbPrimitiveObjectWithSetter>) factory.createSetter(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpDouble", double.class));
		setter.setDouble(object, 3.144);
		assertEquals(3.144, object.getpDouble(), 0);
	}
}
