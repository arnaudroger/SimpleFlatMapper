package org.sfm.csv.cell;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

public class ByteCellValueReaderTest {

	ByteCellValueReader reader = new ByteCellValueReader();
	@Test
	public void testReadByte() throws UnsupportedEncodingException {
		testReadByte(0);
		testReadByte(54);
		testReadByte(-55);
		testReadByte(Byte.MIN_VALUE);
		testReadByte(Byte.MAX_VALUE);
	}
	@Test
	public void testInvalidByte() throws UnsupportedEncodingException {
		final byte[] bytes = "Nan".getBytes("UTF-8");
		try {
			reader.read(bytes, 0, bytes.length);
			fail("Expect exception");
		} catch(ParsingException e){
			// expected
		}
	
	}

	private void testReadByte(int i) throws UnsupportedEncodingException {
		final byte[] bytes = ("_" + Integer.toString(i) + "_").getBytes("UTF-8");
		assertEquals(i, reader.read(bytes, 1, bytes.length-2).byteValue());
	}

}
