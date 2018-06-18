package org.simpleflatmapper.csv.test.impl.cellreader;

import org.junit.Test;
import org.simpleflatmapper.csv.impl.ParsingException;
import org.simpleflatmapper.csv.impl.cellreader.LongCellValueReaderImpl;

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
		} catch(NumberFormatException e){
			// expected
		}
	}

	@Test
	public void testReadEmptyStringReturnNull() {
		assertNull(reader.read(new char[10], 2, 0, null));
	}

	private void testReadLong(long l) throws UnsupportedEncodingException {
		assertEquals(l, readLong(Long.toString(l)));
	}

	private long readLong(String string) {
		final char[] chars = ("_" + string + "_").toCharArray();
		return reader.read(chars, 1, chars.length - 2, null).longValue();
	}

}
