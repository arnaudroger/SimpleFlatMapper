package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.beans.Foo;
import org.sfm.reflect.impl.MethodSetter;
import org.sfm.reflect.primitive.*;

import static org.junit.Assert.assertEquals;

public class MethodSetterTest {

	
	@Test
	public void testSet() throws Exception {
		MethodSetter<Foo, String> setter = new MethodSetter<Foo, String>(Foo.class.getDeclaredMethod("setFoo", String.class));
		SetterTestHelper.validateFooSetter(setter);
	}
	
	DbPrimitiveObjectWithSetter object = new DbPrimitiveObjectWithSetter();
	
	@Test
	public void testSetBoolean() throws Exception {
		BooleanSetter<DbPrimitiveObjectWithSetter> setter = new BooleanMethodSetter<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpBoolean", boolean.class));
		setter.setBoolean(object, true);
		assertEquals(true, object.ispBoolean());
	}
	
	@Test
	public void testSetByte() throws Exception {
		ByteSetter<DbPrimitiveObjectWithSetter> setter = 
				new ByteMethodSetter<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpByte", byte.class));
		setter.setByte(object, (byte)0xc3);
		assertEquals((byte)0xc3, object.getpByte());
	}
	
	@Test
	public void testSetCharacter() throws Exception {
		CharacterSetter<DbPrimitiveObjectWithSetter> setter = 
				 new CharacterMethodSetter<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpCharacter", char.class));
		setter.setCharacter(object, 'g');
		assertEquals('g', object.getpCharacter());
	}
	
	@Test
	public void testSetShort() throws Exception {
		ShortSetter<DbPrimitiveObjectWithSetter> setter = 
				new ShortMethodSetter<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpShort", short.class));
		setter.setShort(object, (short)33);
		assertEquals((short)33, object.getpShort());
	}
	
	@Test
	public void testSetInt() throws Exception {
		IntSetter<DbPrimitiveObjectWithSetter> setter = 
				new IntMethodSetter<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpInt", int.class));
		setter.setInt(object, 35);
		assertEquals(35, object.getpInt());
	}
	
	@Test
	public void testSetLong() throws Exception {
		LongSetter<DbPrimitiveObjectWithSetter> setter = 
				new LongMethodSetter<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpLong", long.class));
		setter.setLong(object, 35l);
		assertEquals(35l, object.getpLong());
	}
	
	@Test
	public void testSetFloat() throws Exception {
		FloatSetter<DbPrimitiveObjectWithSetter> setter = 
				new FloatMethodSetter<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpFloat", float.class));
		setter.setFloat(object, 3.14f);
		assertEquals(3.14f, object.getpFloat(), 0);
	}

	@Test
	public void testSetDouble() throws Exception {
		DoubleSetter<DbPrimitiveObjectWithSetter> setter = 
				new DoubleMethodSetter<DbPrimitiveObjectWithSetter>(DbPrimitiveObjectWithSetter.class.getDeclaredMethod("setpDouble", double.class));
		setter.setDouble(object, 3.144);
		assertEquals(3.144, object.getpDouble(), 0);
	}

}
