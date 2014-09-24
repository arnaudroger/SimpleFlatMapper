package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class LongCellValueReaderTest {

	LongCellValueReader reader = new LongCellValueReader();
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

	private void testReadLong(long l) throws UnsupportedEncodingException {
		final byte[] bytes = ("_" + Long.toString(l) + "_").getBytes("UTF-8");
		final char[] chars = ("_" + Long.toString(l) + "_").toCharArray();
		assertEquals(l, reader.read(bytes, 1, bytes.length -2).longValue());
		assertEquals(l, reader.read(chars, 1, chars.length -2).longValue());
	}

}
