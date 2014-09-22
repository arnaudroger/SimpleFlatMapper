package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class IntegerCellValueReaderTest {

	IntegerCellValueReader reader = new IntegerCellValueReader();
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
		final byte[] bytes = "Nan".getBytes("UTF-8");
		try {
			reader.read(bytes, 0, bytes.length);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
	
	}

	private void testReadInt(int i) throws UnsupportedEncodingException {
		final byte[] bytes = ("_" + Integer.toString(i) + "_").getBytes("UTF-8");
		assertEquals(i, reader.read(bytes, 1, bytes.length-2).intValue());
	}

}
