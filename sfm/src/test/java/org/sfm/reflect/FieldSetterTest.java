package org.sfm.reflect;

import org.junit.Test;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.beans.Foo;
import org.sfm.reflect.impl.FieldSetter;
import org.sfm.reflect.primitive.*;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

public class FieldSetterTest {

	@Test
	public void testSet() throws Exception {
		Field fooField = Foo.class.getDeclaredField("foo");
		fooField.setAccessible(true);
		
		FieldSetter<Foo, String> setter = new FieldSetter<Foo, String>(fooField);
		
		SetterHelperTest.validateFooSetter(setter);
        assertEquals("FieldSetter{field=private java.lang.String org.sfm.beans.Foo.foo}", setter.toString());
	}

	DbPrimitiveObject object = new DbPrimitiveObject();
	
	@Test
	public void testSetBoolean() throws Exception {
		BooleanSetter<DbPrimitiveObject> setter = new BooleanFieldSetter<DbPrimitiveObject>(getField("pBoolean"));
		setter.setBoolean(object, true);
		assertEquals(true, object.ispBoolean());
        assertEquals("BooleanFieldSetter{field=protected boolean org.sfm.beans.DbPrimitiveObject.pBoolean}", setter.toString());
	}
	
	@Test
	public void testSetByte() throws Exception {
		ByteSetter<DbPrimitiveObject> setter = 
				new ByteFieldSetter<DbPrimitiveObject>(getField("pByte"));
		setter.setByte(object, (byte)0xc3);
		assertEquals((byte)0xc3, object.getpByte());
        assertEquals("ByteFieldSetter{field=protected byte org.sfm.beans.DbPrimitiveObject.pByte}", setter.toString());
	}
	
	@Test
	public void testSetCharacter() throws Exception {
		CharacterSetter<DbPrimitiveObject> setter = 
				 new CharacterFieldSetter<DbPrimitiveObject>(getField("pCharacter"));
		setter.setCharacter(object, 'g');
		assertEquals('g', object.getpCharacter());
        assertEquals("CharacterFieldSetter{field=protected char org.sfm.beans.DbPrimitiveObject.pCharacter}", setter.toString());
	}
	
	@Test
	public void testSetShort() throws Exception {
		ShortSetter<DbPrimitiveObject> setter = 
				new ShortFieldSetter<DbPrimitiveObject>(getField("pShort"));
		setter.setShort(object, (short)33);
		assertEquals((short)33, object.getpShort());
        assertEquals("ShortFieldSetter{field=protected short org.sfm.beans.DbPrimitiveObject.pShort}", setter.toString());
	}
	
	@Test
	public void testSetInt() throws Exception {
		IntSetter<DbPrimitiveObject> setter = 
				new IntFieldSetter<DbPrimitiveObject>(getField("pInt"));
		setter.setInt(object, 35);
		assertEquals(35, object.getpInt());
        assertEquals("IntFieldSetter{field=protected int org.sfm.beans.DbPrimitiveObject.pInt}", setter.toString());
	}
	
	@Test
	public void testSetLong() throws Exception {
		LongSetter<DbPrimitiveObject> setter = 
				new LongFieldSetter<DbPrimitiveObject>(getField("pLong"));
		setter.setLong(object, 35l);
		assertEquals(35l, object.getpLong());
        assertEquals("LongFieldSetter{field=protected long org.sfm.beans.DbPrimitiveObject.pLong}", setter.toString());
	}
	
	@Test
	public void testSetFloat() throws Exception {
		FloatSetter<DbPrimitiveObject> setter = 
				new FloatFieldSetter<DbPrimitiveObject>(getField("pFloat"));
		setter.setFloat(object, 3.14f);
		assertEquals(3.14f, object.getpFloat(), 0);
        assertEquals("FloatFieldSetter{field=protected float org.sfm.beans.DbPrimitiveObject.pFloat}", setter.toString());
	}

	@Test
	public void testSetDouble() throws Exception {
		DoubleSetter<DbPrimitiveObject> setter = 
				new DoubleFieldSetter<DbPrimitiveObject>(getField("pDouble"));
		setter.setDouble(object, 3.144);
		assertEquals(3.144, object.getpDouble(), 0);
        assertEquals("DoubleFieldSetter{field=protected double org.sfm.beans.DbPrimitiveObject.pDouble}", setter.toString());
	}

	public Field getField(String name) throws NoSuchFieldException {
		Field f =  DbPrimitiveObject.class.getDeclaredField(name);
		f.setAccessible(true);
		return f;
	}


}
