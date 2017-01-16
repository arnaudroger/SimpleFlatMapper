package org.simpleflatmapper.csv.test.impl.cellreader;

import org.junit.Test;
import org.simpleflatmapper.csv.impl.cellreader.DoubleCellValueReaderImpl;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class DoubleCellValueReaderTest {

	DoubleCellValueReaderImpl reader = new DoubleCellValueReaderImpl();
	@Test
	public void testReadDouble() throws UnsupportedEncodingException {
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
	public void testDoubleWithLeadingSpace() {
		assertEquals(1.234, readDouble(" 1.234"), 0.00001);
	}

	@Test
	public void testReadEmptyStringReturnNull() {
		assertNull(reader.read(new char[10], 2, 0, null));
	}

	private void testReadDouble(double i) throws UnsupportedEncodingException {
		assertEquals(i, readDouble(Double.toString(i)), 0);
	}

	private double readDouble(String string) {
		final char[] chars = ("_" + string + "_").toCharArray();
		return reader.read(chars, 1, chars.length - 2, null).doubleValue();
	}

}
