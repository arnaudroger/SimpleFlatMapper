package org.sfm.csv.impl.primitive;

import org.junit.Test;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.csv.impl.cellreader.*;
import org.sfm.reflect.Setter;
import org.sfm.reflect.SetterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PrimitivesDelayedCellSetterTest {

	SetterFactory setterFactory = new SetterFactory(null);
	
	@Test
	public void testBooleanGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Boolean> setter = setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean");
		BooleanDelayedCellSetter<DbPrimitiveObjectWithSetter> bsetter = 
				new BooleanDelayedCellSetter<DbPrimitiveObjectWithSetter>(SetterFactory.toBooleanSetter(setter), new BooleanCellValueReaderImpl());
		bsetter.set("true".toCharArray(), 0, 4, null);
		assertTrue(bsetter.getValue());
		
		bsetter.set("true".toCharArray(), 0, 4, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		bsetter.set(o);
		assertTrue(o.ispBoolean());
	}

	@Test
	public void testByteGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Byte> byteSetter = setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte");
		ByteDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new ByteDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toByteSetter(byteSetter), new ByteCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Byte((byte)13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((byte)13, o.getpByte());
	}
	
	@Test
	public void testCharGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Character> charSetter = setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pChar");
		CharDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new CharDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toCharacterSetter(charSetter), new CharCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Character((char)13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((char)13, o.getpCharacter());
	}

	@Test
	public void testShortGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Short> shortSetter = setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort");
		ShortDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new ShortDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toShortSetter(shortSetter), new ShortCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Short((short)13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((short)13, o.getpShort());
	}
	
	@Test
	public void testIntGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Integer> intSetter = setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt");
		IntDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new IntDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toIntSetter(intSetter), new IntegerCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Integer(13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpInt());
	}
	
	@Test
	public void testLongGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Long> longSetter = setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong");
		LongDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new LongDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toLongSetter(longSetter), new LongCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Long(13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpLong());
	}

	@Test
	public void testFloatGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Float> floatSetter = setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat");
		FloatDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new FloatDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toFloatSetter(floatSetter), new FloatCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Float(13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpFloat(), 0);
	}

	@Test
	public void testDoubleGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Double> doubleSetter = setterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble");
		DoubleDelayedCellSetter<DbPrimitiveObjectWithSetter> setter = 
				new DoubleDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						SetterFactory.toDoubleSetter(doubleSetter), new DoubleCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Double(13), setter.getValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpDouble(), 0);
	}	
}
