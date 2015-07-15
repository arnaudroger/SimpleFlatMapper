package org.sfm.csv.impl.cellreader;

import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

public class FloatCellValueReaderTest {

	FloatCellValueReaderImpl reader = new FloatCellValueReaderImpl();
	@Test
	public void testReadInt() throws UnsupportedEncodingException {
		testReadFloat(0);
		testReadFloat(12345.33f);
		testReadFloat(-12345.33f);
		testReadFloat(Float.MIN_VALUE);
		testReadFloat(Float.MAX_VALUE);
	}
	
	@Test
	public void testInvalidFloat() throws UnsupportedEncodingException {
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

	private void testReadFloat(float i) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Float.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).floatValue(), 0);
	}

}
