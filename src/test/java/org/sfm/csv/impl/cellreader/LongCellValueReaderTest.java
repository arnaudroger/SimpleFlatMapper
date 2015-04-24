package org.sfm.csv.impl.cellreader;

import org.junit.Test;
import org.sfm.csv.impl.ParsingException;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

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

	@Test
	public void testReadEmptyStringReturnNull() {
		assertNull(reader.read(new char[10], 2, 0, null));
	}

	private void testReadLong(long l) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Long.toString(l) + "_").toCharArray();
		assertEquals(l, reader.read(chars, 1, chars.length -2, null).longValue());
	}

}
