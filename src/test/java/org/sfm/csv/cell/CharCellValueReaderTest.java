package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class CharCellValueReaderTest {

	CharCellValueReader reader = new CharCellValueReader();
	@Test
	public void testReadChar() throws UnsupportedEncodingException {
		testReadShort(345);
		testReadShort(Character.MIN_VALUE);
		testReadShort(Character.MAX_VALUE);
	}
	
	@Test
	public void testInvalidChar() throws UnsupportedEncodingException {
		final byte[] bytes = "Nan".getBytes("UTF-8");
		final char[] chars = "Nan".toCharArray();
		try {
			reader.read(bytes, 0, bytes.length, null);
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

	private void testReadShort(int i) throws UnsupportedEncodingException {
		final byte[] bytes = ("_" + Integer.toString(i) + "_").getBytes("UTF-8");
		final char[] chars = ("_" + Integer.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(bytes, 1, bytes.length-2, null).charValue());
		assertEquals(i, reader.read(chars, 1, chars.length-2).charValue());
	}

}
