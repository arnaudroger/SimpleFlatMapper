package org.sfm.csv.primitive;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.reflect.SetterFactory;

public class PrimitivesDelayedCellSetterTest {

	SetterFactory setterFactory = new SetterFactory(null);
	
	@Test
	public void testBooleanGetSetValue() throws Exception {
		BooleanDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new BooleanDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toBooleanSetter(setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean")));
		setter.set("true".toCharArray(), 0, 4, null);
		assertTrue(setter.getValue());
		
		setter.set("true".toCharArray(), 0, 4, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertTrue(o.ispBoolean());
	}

	@Test
	public void testByteGetSetValue() throws Exception {
		ByteDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new ByteDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toByteSetter(setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte")));
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Byte((byte)13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((byte)13, o.getpByte());
	}
	
	@Test
	public void testCharGetSetValue() throws Exception {
		CharDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new CharDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toCharacterSetter(setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pChar")));
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Character((char)13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((char)13, o.getpCharacter());
	}

	@Test
	public void testShortGetSetValue() throws Exception {
		ShortDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new ShortDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toShortSetter(setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort")));
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Short((short)13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((short)13, o.getpShort());
	}
	
	@Test
	public void testIntGetSetValue() throws Exception {
		IntDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new IntDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toIntSetter(setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt")));
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Integer(13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpInt());
	}
	
	@Test
	public void testLongGetSetValue() throws Exception {
		LongDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new LongDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toLongSetter(setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong")));
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Long(13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpLong());
	}

	@Test
	public void testFloatGetSetValue() throws Exception {
		FloatDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new FloatDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toFloatSetter(setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat")));
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Float(13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpFloat(), 0);
	}

	@Test
	public void testDoubleGetSetValue() throws Exception {
		DoubleDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new DoubleDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toDoubleSetter(setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble")));
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Double(13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpDouble(), 0);
	}	
}
