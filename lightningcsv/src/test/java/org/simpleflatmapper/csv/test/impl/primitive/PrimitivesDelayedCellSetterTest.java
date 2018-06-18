package org.simpleflatmapper.csv.test.impl.primitive;

import org.junit.Test;
import org.simpleflatmapper.csv.impl.primitive.BooleanDelayedCellSetter;
import org.simpleflatmapper.csv.impl.primitive.ByteDelayedCellSetter;
import org.simpleflatmapper.csv.impl.primitive.CharDelayedCellSetter;
import org.simpleflatmapper.csv.impl.primitive.DoubleDelayedCellSetter;
import org.simpleflatmapper.csv.impl.primitive.FloatDelayedCellSetter;
import org.simpleflatmapper.csv.impl.primitive.IntDelayedCellSetter;
import org.simpleflatmapper.csv.impl.primitive.LongDelayedCellSetter;
import org.simpleflatmapper.csv.impl.primitive.ShortDelayedCellSetter;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.csv.impl.cellreader.*;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.ObjectSetterFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PrimitivesDelayedCellSetterTest {

	ObjectSetterFactory objectSetterFactory = new ObjectSetterFactory(null);
	
	@Test
	public void testBooleanGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Boolean> setter = objectSetterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pBoolean");
		BooleanDelayedCellSetter<DbPrimitiveObjectWithSetter> bSetter =
				new BooleanDelayedCellSetter<DbPrimitiveObjectWithSetter>(ObjectSetterFactory.toBooleanSetter(setter), new BooleanCellValueReaderImpl());
		bSetter.set("true".toCharArray(), 0, 4, null);
		assertTrue(bSetter.consumeValue());
		
		bSetter.set("true".toCharArray(), 0, 4, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		bSetter.set(o);
		assertTrue(o.ispBoolean());

        bSetter.set("".toCharArray(), 0, 0, null);
        assertNull(bSetter.consumeValue());

    }

	@Test
	public void testByteGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Byte> byteSetter = objectSetterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pByte");
		ByteDelayedCellSetter<DbPrimitiveObjectWithSetter> setter =
				new ByteDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						ObjectSetterFactory.toByteSetter(byteSetter), new ByteCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Byte((byte)13), setter.consumeValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((byte)13, o.getpByte());

        setter.set("".toCharArray(), 0, 0, null);
        assertNull(setter.consumeValue());

    }
	
	@Test
	public void testCharGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Character> charSetter = objectSetterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pChar");
		CharDelayedCellSetter<DbPrimitiveObjectWithSetter> setter =
				new CharDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						ObjectSetterFactory.toCharacterSetter(charSetter), new CharCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Character((char)13), setter.consumeValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((char)13, o.getpCharacter());

        setter.set("".toCharArray(), 0, 0, null);
        assertNull(setter.consumeValue());

    }

	@Test
	public void testShortGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Short> shortSetter = objectSetterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pShort");
		ShortDelayedCellSetter<DbPrimitiveObjectWithSetter> setter =
				new ShortDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						ObjectSetterFactory.toShortSetter(shortSetter), new ShortCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Short((short)13), setter.consumeValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals((short)13, o.getpShort());

        setter.set("".toCharArray(), 0, 0, null);
        assertNull(setter.consumeValue());

    }
	
	@Test
	public void testIntGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Integer> intSetter = objectSetterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pInt");
		IntDelayedCellSetter<DbPrimitiveObjectWithSetter> setter =
				new IntDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						ObjectSetterFactory.toIntSetter(intSetter), new IntegerCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Integer(13), setter.consumeValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpInt());

        setter.set("".toCharArray(), 0, 0, null);
        assertNull(setter.consumeValue());

    }
	
	@Test
	public void testLongGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Long> longSetter = objectSetterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pLong");
		LongDelayedCellSetter<DbPrimitiveObjectWithSetter> setter =
				new LongDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						ObjectSetterFactory.toLongSetter(longSetter), new LongCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Long(13), setter.consumeValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpLong());

        setter.set("".toCharArray(), 0, 0, null);
        assertNull(setter.consumeValue());

    }

	@Test
	public void testFloatGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Float> floatSetter = objectSetterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pFloat");
		FloatDelayedCellSetter<DbPrimitiveObjectWithSetter> setter =
				new FloatDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						ObjectSetterFactory.toFloatSetter(floatSetter), new FloatCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Float(13), setter.consumeValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpFloat(), 0);

        setter.set("".toCharArray(), 0, 0, null);
        assertNull(setter.consumeValue());

    }

	@Test
	public void testDoubleGetSetValue() throws Exception {
		Setter<DbPrimitiveObjectWithSetter, Double> doubleSetter = objectSetterFactory.getSetter(DbPrimitiveObjectWithSetter.class, "pDouble");
		DoubleDelayedCellSetter<DbPrimitiveObjectWithSetter> setter =
				new DoubleDelayedCellSetter<DbPrimitiveObjectWithSetter>(
						ObjectSetterFactory.toDoubleSetter(doubleSetter), new DoubleCellValueReaderImpl());
		setter.set("13".toCharArray(), 0, 2, null);
		assertEquals(new Double(13), setter.consumeValue());
		
		setter.set("13".toCharArray(), 0, 2, null);
		DbPrimitiveObjectWithSetter o = new DbPrimitiveObjectWithSetter();
		setter.set(o);
		assertEquals(13, o.getpDouble(), 0);

        setter.set("".toCharArray(), 0, 0, null);
        assertNull(setter.consumeValue());

    }
}
