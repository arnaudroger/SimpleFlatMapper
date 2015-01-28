package org.sfm.csv.impl.cellreader;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class DoubleCellValueReaderTest {

	DoubleCellValueReaderImpl reader = new DoubleCellValueReaderImpl();
	@Test
	public void testReadInt() throws UnsupportedEncodingException {
		testReadDouble(0);
		testReadDouble(12345.33);
		testReadDouble(-12345.33);
		testReadDouble(Double.MIN_VALUE);
		testReadDouble(Double.MAX_VALUE);
	}
	
	@Test
	public void testInvalidDouble() throws UnsupportedEncodingException {
		final char[] chars = "ddd".toCharArray();
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

	private void testReadDouble(double i) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Double.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).doubleValue(), 0);
	}

}
