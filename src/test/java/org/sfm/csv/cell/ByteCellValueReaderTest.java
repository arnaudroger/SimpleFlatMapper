package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class ByteCellValueReaderTest {

	ByteCellValueReader reader = new ByteCellValueReader();
	@Test
	public void testReadByte() throws UnsupportedEncodingException {
		testRead(0);
		testRead(54);
		testRead(-55);
		testRead(Byte.MIN_VALUE);
		testRead(Byte.MAX_VALUE);
	}
	@Test
	public void testInvalid() throws UnsupportedEncodingException {
		final byte[] bytes = "Nan".getBytes("UTF-8");
		final char[] chars = "Nan".toCharArray();
		try {
			reader.read(bytes, 0, bytes.length);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
		try {
			reader.read(chars, 0, chars.length);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
	
	}

	private void testRead(int i) throws UnsupportedEncodingException {
		final byte[] bytes = ("_" + Integer.toString(i) + "_").getBytes("UTF-8");
		final char[] chars = ("_" + Integer.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(bytes, 1, bytes.length-2).byteValue());
		assertEquals(i, reader.read(chars, 1, chars.length-2).byteValue());
	}

}
