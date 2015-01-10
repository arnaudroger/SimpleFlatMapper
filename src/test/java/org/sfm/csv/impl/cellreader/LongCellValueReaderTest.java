package org.sfm.csv.impl.cellreader;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;

public class LongCellValueReaderTest {

	LongCellValueReaderImpl reader = new LongCellValueReaderImpl();
	@Test
	public void testReadLong() throws UnsupportedEncodingException {
		testReadLong(0);
		testReadLong(12345);
		testReadLong(-12345);
		testReadLong(Long.MIN_VALUE);
		testReadLong(Long.MAX_VALUE);
	}
	
	@Test
	public void testInvalidLong() throws UnsupportedEncodingException {
		final char[] chars = "Nan".toCharArray();
		try {
			reader.read(chars, 0, chars.length, null);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
	}

	private void testReadLong(long l) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Long.toString(l) + "_").toCharArray();
		assertEquals(l, reader.read(chars, 1, chars.length -2, null).longValue());
	}

}
