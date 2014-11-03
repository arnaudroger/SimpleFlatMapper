package org.sfm.csv.impl.cellreader;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;
import org.sfm.csv.impl.cellreader.CharCellValueReader;

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
		final char[] chars = "Nan".toCharArray();
		try {
			reader.read(chars, 0, chars.length, null);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}	
	}

	private void testReadShort(int i) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Integer.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).charValue());
	}

}
