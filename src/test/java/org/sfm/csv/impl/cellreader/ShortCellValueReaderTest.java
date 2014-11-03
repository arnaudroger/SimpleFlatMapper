package org.sfm.csv.impl.cellreader;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;
import org.sfm.csv.impl.cellreader.ShortCellValueReader;

public class ShortCellValueReaderTest {

	ShortCellValueReader reader = new ShortCellValueReader();
	@Test
	public void testReadShort() throws UnsupportedEncodingException {
		testReadShort(0);
		testReadShort(12345);
		testReadShort(-12345);
		testReadShort(Short.MIN_VALUE);
		testReadShort(Short.MAX_VALUE);
	}
	
	@Test
	public void testInvalidShort() throws UnsupportedEncodingException {
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
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).shortValue());
	}

}
