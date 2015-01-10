package org.sfm.csv.impl.cellreader;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;

public class IntegerCellValueReaderTest {

	IntegerCellValueReaderImpl reader = new IntegerCellValueReaderImpl();
	@Test
	public void testReadInt() throws UnsupportedEncodingException {
		testReadInt(0);
		testReadInt(12345);
		testReadInt(-12345);
		testReadInt(Integer.MIN_VALUE);
		testReadInt(Integer.MAX_VALUE);
	}
	
	@Test
	public void testInvalidInt() throws UnsupportedEncodingException {
		final char[] chars = "Nan".toCharArray();
		try {
			reader.read(chars, 0, chars.length, null);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
	}

	private void testReadInt(int i) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Integer.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).intValue());
	}

}
