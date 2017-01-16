package org.simpleflatmapper.csv.test.impl.cellreader;

import org.junit.Test;
import org.simpleflatmapper.csv.impl.ParsingException;
import org.simpleflatmapper.csv.impl.cellreader.ShortCellValueReaderImpl;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class ShortCellValueReaderTest {

	ShortCellValueReaderImpl reader = new ShortCellValueReaderImpl();
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
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).shortValue());
	}

}
