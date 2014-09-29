package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.sfm.csv.DecoderContext;

public class FloatCellValueReaderTest {

	FloatCellValueReader reader = new FloatCellValueReader();
	@Test
	public void testReadInt() throws UnsupportedEncodingException {
		testReadFloat(0);
		testReadFloat(12345.33f);
		testReadFloat(-12345.33f);
		testReadFloat(Float.MIN_VALUE);
		testReadFloat(Float.MAX_VALUE);
	}
	
	@Test
	public void testInvalidFloat() throws UnsupportedEncodingException {
		final byte[] bytes = "ddd".getBytes("UTF-8");
		final char[] chars = "ddd".toCharArray();
		try {
			reader.read(bytes, 0, bytes.length, DecoderContext.forCharset("UTF-8"));
			fail("Expect exception");
		} catch(NumberFormatException e){
			// expected
		}
		try {
			reader.read(chars, 0, chars.length);
			fail("Expect exception");
		} catch(NumberFormatException e){
			// expected
		}
	}

	private void testReadFloat(float i) throws UnsupportedEncodingException {
		final byte[] bytes = ("_" + Float.toString(i) + "_").getBytes("UTF-8");
		final char[] chars = ("_" + Float.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(bytes, 1, bytes.length-2, DecoderContext.forCharset("UTF-8")).floatValue(), 0);
		assertEquals(i, reader.read(chars, 1, chars.length-2).floatValue(), 0);
	}

}
