package org.sfm.csv.cell;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class FloatCellValueReaderTest {

	FloatCellValueReader reader = new FloatCellValueReader();
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

	private void testReadFloat(float i) throws UnsupportedEncodingException {
		final char[] chars = ("_" + Float.toString(i) + "_").toCharArray();
		assertEquals(i, reader.read(chars, 1, chars.length-2, null).floatValue(), 0);
	}

}
