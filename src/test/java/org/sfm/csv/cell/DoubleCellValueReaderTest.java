package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class DoubleCellValueReaderTest {

	DoubleCellValueReader reader = new DoubleCellValueReader();
	@Test
	public void testReadInt() throws UnsupportedEncodingException {
		testReadDouble(0);
		testReadDouble(12345.33);
		testReadDouble(-12345.33);
		testReadDouble(Double.MIN_VALUE);
		testReadDouble(Double.MAX_VALUE);
	}
	
	@Test
	public void testInvalidDouble() throws UnsupportedEncodingException {
		final byte[] bytes = "ddd".getBytes("UTF-8");
		final char[] chars = "ddd".toCharArray();
		try {
			reader.read(bytes, 0, bytes.length);
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

	private void testReadDouble(double i) throws UnsupportedEncodingException {
		final byte[] bytes = ("_" + Double.toString(i) + "_").getBytes("UTF-8");
		final char[] chars = ("_" + Double.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(bytes, 1, bytes.length-2).doubleValue(), 0);
		assertEquals(i, reader.read(chars, 1, chars.length-2).doubleValue(), 0);
	}

}
