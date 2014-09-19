package org.sfm.csv.cell;

import static org.junit.Assert.*;

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
	
	public void testInvalidFloat() throws UnsupportedEncodingException {
		final byte[] bytes = "ddd".getBytes("UTF-8");
		try {
			reader.read(bytes, 0, bytes.length);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
	
	}

	private void testReadFloat(float i) throws UnsupportedEncodingException {
		final byte[] bytes = ("_" + Float.toString(i) + "_").getBytes("UTF-8");
		assertEquals(i, reader.read(bytes, 1, bytes.length-2).floatValue(), 0);
	}

}
