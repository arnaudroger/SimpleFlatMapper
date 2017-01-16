package org.simpleflatmapper.csv.test.impl.cellreader;

import org.junit.Test;
import org.simpleflatmapper.csv.impl.ParsingException;
import org.simpleflatmapper.csv.impl.cellreader.CharCellValueReaderImpl;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class CharCellValueReaderTest {

	CharCellValueReaderImpl reader = new CharCellValueReaderImpl();
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
		} catch(NumberFormatException e){
			// expected
		}	
	}

	@Test
	public void testReadEmptyStringReturnNull() {
		assertNull(reader.read(new char[10], 2, 0, null));
	}

	private void testReadShort(int i) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Integer.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).charValue());
	}

}
