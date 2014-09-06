package org.sfm.reflect;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;

import org.junit.Test;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.beans.Foo;
import org.sfm.reflect.impl.FieldSetter;
import org.sfm.reflect.primitive.BooleanFieldSetter;
import org.sfm.reflect.primitive.BooleanSetter;
import org.sfm.reflect.primitive.ByteFieldSetter;
import org.sfm.reflect.primitive.ByteSetter;
import org.sfm.reflect.primitive.CharacterFieldSetter;
import org.sfm.reflect.primitive.CharacterSetter;
import org.sfm.reflect.primitive.DoubleFieldSetter;
import org.sfm.reflect.primitive.DoubleSetter;
import org.sfm.reflect.primitive.FloatFieldSetter;
import org.sfm.reflect.primitive.FloatSetter;
import org.sfm.reflect.primitive.IntFieldSetter;
import org.sfm.reflect.primitive.IntSetter;
import org.sfm.reflect.primitive.LongFieldSetter;
import org.sfm.reflect.primitive.LongSetter;
import org.sfm.reflect.primitive.ShortFieldSetter;
import org.sfm.reflect.primitive.ShortSetter;

public class FieldSetterTest {

	@Test
	public void testSet() throws Exception {
		Field fooField = Foo.class.getDeclaredField("foo");
		fooField.setAccessible(true);
		
		FieldSetter<Foo, String> setter = new FieldSetter<Foo, String>(fooField);
		
		SetterTestHelper.validateFooSetter(setter);
	}

	DbPrimitiveObject object = new DbPrimitiveObject();
	
	@Test
	public void testSetBoolean() throws Exception {
		BooleanSetter<DbPrimitiveObject> setter = new BooleanFieldSetter<DbPrimitiveObject>(getField("pBoolean"));
		setter.setBoolean(object, true);
		assertEquals(true, object.ispBoolean());
	}
	
	@Test
	public void testSetByte() throws Exception {
		ByteSetter<DbPrimitiveObject> setter = 
				new ByteFieldSetter<DbPrimitiveObject>(getField("pByte"));
		setter.setByte(object, (byte)0xc3);
		assertEquals((byte)0xc3, object.getpByte());
	}
	
	@Test
	public void testSetCharacter() throws Exception {
		CharacterSetter<DbPrimitiveObject> setter = 
				 new CharacterFieldSetter<DbPrimitiveObject>(getField("pCharacter"));
		setter.setCharacter(object, 'g');
		assertEquals('g', object.getpCharacter());
	}
	
	@Test
	public void testSetShort() throws Exception {
		ShortSetter<DbPrimitiveObject> setter = 
				new ShortFieldSetter<DbPrimitiveObject>(getField("pShort"));
		setter.setShort(object, (short)33);
		assertEquals((short)33, object.getpShort());
	}
	
	@Test
	public void testSetInt() throws Exception {
		IntSetter<DbPrimitiveObject> setter = 
				new IntFieldSetter<DbPrimitiveObject>(getField("pInt"));
		setter.setInt(object, 35);
		assertEquals(35, object.getpInt());
	}
	
	@Test
	public void testSetLong() throws Exception {
		LongSetter<DbPrimitiveObject> setter = 
				new LongFieldSetter<DbPrimitiveObject>(getField("pLong"));
		setter.setLong(object, 35l);
		assertEquals(35l, object.getpLong());
	}
	
	@Test
	public void testSetFloat() throws Exception {
		FloatSetter<DbPrimitiveObject> setter = 
				new FloatFieldSetter<DbPrimitiveObject>(getField("pFloat"));
		setter.setFloat(object, 3.14f);
		assertEquals(3.14f, object.getpFloat(), 0);
	}

	@Test
	public void testSetDouble() throws Exception {
		DoubleSetter<DbPrimitiveObject> setter = 
				new DoubleFieldSetter<DbPrimitiveObject>(getField("pDouble"));
		setter.setDouble(object, 3.144);
		assertEquals(3.144, object.getpDouble(), 0);
	}

	public Field getField(String name) throws NoSuchFieldException {
		Field f =  DbPrimitiveObject.class.getDeclaredField(name);
		f.setAccessible(true);
		return f;
	}


}
